package com.bob.scala.webapi.utils

import java.util.concurrent.TimeUnit

import redis.clients.jedis.JedisCluster

/**
 * Created by bob on 16/3/21.
 */
trait DistributeLock {

  /**
   * 默认锁有效时间(单位毫秒)
   */
  val DEFAULT_LOCK_EXPIRE_TIME = 60000L

  /**
   * 默认睡眠时间(单位毫秒)
   */
  val DEFAULT_SLEEP_TIME = 100L

  /**
   * 尝试锁
   *
   * @param lock 锁的键
   * @param requestTimeout 请求超时 ms
   * @return 如果锁成功，则返回true；否则返回false
   */
  def tryLock(lock: String, requestTimeout: Long): Boolean

  /**
   * 尝试锁
   * @param lock 锁的键
   * @param lockExpireTime 锁有效期 ms
   * @param requestTimeout 请求超时 ms
   * @return 如果锁成功，则返回true；否则返回false
   */
  def tryLock(lock: String, lockExpireTime: Long, requestTimeout: Long): Boolean

  /**
   * 解锁
   * @param lock 锁的键
   */
  def unlock(lock: String): Unit
}

/**
 * 使用redis做为分布式锁
 */
class RedisDistributeLock(jedisCluster: JedisCluster) extends DistributeLock {

  import java.lang.{Long => JLong}

  /**
   * 尝试锁
   *
   * @param lock 锁的键
   * @param requestTimeout 请求超时 ms
   * @return 如果锁成功，则返回true；否则返回false
   */
  override def tryLock(lock: String, requestTimeout: Long): Boolean = {
    this.tryLock(lock, DEFAULT_LOCK_EXPIRE_TIME, requestTimeout)
  }

  /**
   * 尝试锁
   * @param lock 锁的键
   * @param lockExpireTime 锁有效期 ms
   * @param requestTimeout 请求超时 ms
   * @return 如果锁成功，则返回true；否则返回false
   */
  override def tryLock(lock: String, lockExpireTime: Long, requestTimeout: Long): Boolean = {
    require(lock != null && lock.trim.length > 0, "lock invalid")
    require(lockExpireTime > 0)
    require(requestTimeout > 0)

    var requireTimeout = requestTimeout
    while (requireTimeout > 0) {
      val expire: String = String.valueOf(System.currentTimeMillis + lockExpireTime + 1)
      val result: Long = jedisCluster.setnx(lock, expire)
      if (result > 0) {
        // 目前没有线程占用此锁
        jedisCluster.expire(lock, JLong.valueOf(lockExpireTime / 1000).intValue)
        return true
      }
      val currentValue: String = jedisCluster.get(lock)
      if (currentValue != null) {
        if (JLong.parseLong(currentValue) < System.currentTimeMillis) {
          val oldValue: String = jedisCluster.getSet(lock, expire)
          if (oldValue == null || (oldValue != null && (oldValue == currentValue))) {
            jedisCluster.expire(lock, JLong.valueOf(lockExpireTime / 1000).intValue)
            return true
          }
        }
      }
      var sleepTime: Long = 0
      if (requestTimeout > DEFAULT_SLEEP_TIME) {
        sleepTime = DEFAULT_SLEEP_TIME
        requireTimeout -= DEFAULT_SLEEP_TIME
      }
      else {
        sleepTime = requestTimeout
        requireTimeout = 0
      }
      try {
        TimeUnit.MILLISECONDS.sleep(sleepTime)
      }
      catch {
        case e: InterruptedException => {

        }
      }
    }
    false
  }

  /**
   * 解锁
   * @param lock 锁的键
   */
  override def unlock(lock: String): Unit = {
    val value: String = jedisCluster.get(lock)
    if (value != null && JLong.parseLong(value) > System.currentTimeMillis) {
      jedisCluster.del(lock)
    }
  }
}