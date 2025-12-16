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
export const publicCheckin = (trainingId, username, employeeNo) => {
  return request({
    url: '/checkins/public',
    method: 'post',
    data: { trainingId, username, employeeNo }
  })
}


