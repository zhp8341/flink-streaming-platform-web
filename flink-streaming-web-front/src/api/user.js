import request from '@/utils/request'
import Qs from 'qs'

export function login(data) {
  const logindata = { name: data.username, password: data.password }
  return request({
    url: '/login', // url: '/vue-element-admin/user/login',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    method: 'post',
    data: Qs.stringify(logindata)
  })
}

export function getInfo(token) {
  return request({
    url: '/getUserInfo', // url: '/vue-element-admin/user/info',
    method: 'get'
  })
}

export function logout() {
  return request({
    url: '/logout', // url: '/vue-element-admin/user/logout',
    method: 'post'
  })
}

/**
 *
 * @param {*} data (userid,fullname)
 * @returns
 */
export function updateUserInfo(data) {
  return request({
    url: '/updateUserInfo',
    method: 'post',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    transformRequest: [function(data) {
      return Qs.stringify(data)
    }],
    data: data
  })
}

/**
 * 更改当前用户密码
 * @param {*} userid
 * @param {*} password
 * @returns
 */
export function updateCurrentUserPassword(userid, password) {
  return request({
    url: '/updateCurrentUserPassword',
    method: 'post',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    transformRequest: [function(data) {
      return Qs.stringify(data)
    }],
    data: {
      userid: userid,
      password: password
    }
  })
}

/**
 * 分页查询用户列表
 * @param {*} pageNum
 * @param {*} pageSize
 * @returns
 */
export function userList(pageNum, pageSize) {
  return request({
    url: '/userList',
    method: 'post',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    transformRequest: [function(data) {
      return Qs.stringify(data)
    }],
    data: {
      pageNum: pageNum,
      pageSize: pageSize
    }
  })
}

/**
 * 启停帐号
 * @param {*} name
 * @param {*} code
 * @returns
 */
export function stopOrOpen(name, code) {
  return request({
    url: '/stopOrOpen',
    method: 'post',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    transformRequest: [function(data) {
      return Qs.stringify(data)
    }],
    data: {
      name: name,
      code: code
    }
  })
}

/**
 * 新增用户
 * @param {*} data (name,fullname,pwd1,pwd2)
 * @returns
 */
export function addUser(data) {
  return request({
    url: '/addUser',
    method: 'post',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    transformRequest: [function(data) {
      return Qs.stringify(data)
    }],
    data: data
  })
}

/**
 * 修改用户密码
 * @param {*} data (name,oldPwd,pwd1,pwd2)
 * @returns
 */
export function updatePassword(data) {
  return request({
    url: '/updatePassword',
    method: 'post',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    transformRequest: [function(data) {
      return Qs.stringify(data)
    }],
    data: data
  })
}

