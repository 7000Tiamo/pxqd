import request from './request'

export const checkin = (trainingId, userId, latitude, longitude) => {
  return request({
    url: '/checkins',
    method: 'post',
    data: { trainingId, userId, latitude, longitude }
  })
}

export const checkout = (trainingId, userId) => {
  return request({
    url: '/checkins/checkout',
    method: 'post',
    data: { trainingId, userId }
  })
}

export const getUserCheckins = (userId) => {
  return request({
    url: `/checkins/user/${userId}`,
    method: 'get'
  })
}

export const getTrainingCheckins = (trainingId) => {
  return request({
    url: `/checkins/training/${trainingId}`,
    method: 'get'
  })
}

export const getCheckinStats = (trainingId) => {
  return request({
    url: `/checkins/stats/${trainingId}`,
    method: 'get'
  })
}

// 公开扫码签到（通过用户名和工号）
export const publicCheckin = (trainingId, username, employeeNo, token) => {
  return request({
    url: '/checkins/public',
    method: 'post',
    data: { trainingId, username, employeeNo, token }
  })
}

// 公开扫码签退（通过用户名和工号）
export const publicCheckout = (trainingId, username, employeeNo, token) => {
  return request({
    url: '/checkins/public/checkout',
    method: 'post',
    data: { trainingId, username, employeeNo, token }
  })
}

// 检查签到二维码是否有效
export const isCheckinActive = (trainingId, token) => {
  return request({
    url: '/checkins/isCheckinActive',
    method: 'post',
    params: { trainingId, token }
  })
}

// 检查签退二维码是否有效
export const isCheckoutActive = (trainingId, token) => {
  return request({
    url: '/checkins/isCheckoutActive',
    method: 'post',
    params: { trainingId, token }
  })
}


