import request from '@/utils/request'
import Qs from 'qs'

/**
 * 查询告警配置信息
 * @returns
 */
export function alartConfig() {
  return request({
    url: '/alartConfig',
    method: 'post',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    transformRequest: [function(data) { return Qs.stringify(data) }],
    data: { }
  })
}

/**
 * 查询配置信息
 * @returns
 */
export function sysConfig() {
  return request({
    url: '/sysConfig',
    method: 'post',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    transformRequest: [function(data) { return Qs.stringify(data) }],
    data: { }
  })
}

/**
 * 更新配置信息
 * @param {*} key
 * @param {*} value
 * @returns
 */
export function upsertSynConfig(key, value) {
  return request({
    url: '/upsertSynConfig',
    method: 'post',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    transformRequest: [function(data) { return Qs.stringify(data) }],
    data: {
      key: key,
      val: value
    }
  })
}

/**
 * test
 * @param {*} key
 * @returns
 */
export function test_alarm(url) {
  return request({
    url: url,
    method: 'post',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
  })
}

/**
 * 删除配置
 * @param {*} key
 * @returns
 */
export function deleteConfig(key) {
  return request({
    url: '/deleteConfig',
    method: 'post',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    transformRequest: [function(data) { return Qs.stringify(data) }],
    data: {
      key: key
    }
  })
}
