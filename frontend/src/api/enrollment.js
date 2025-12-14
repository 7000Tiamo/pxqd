import request from './request'

export const enroll = (trainingId, userId) => {
  return request({
    url: '/enrollments',
    method: 'post',
    data: { trainingId, userId }
  })
}

export const cancelEnrollment = (trainingId, userId) => {
  return request({
    url: '/enrollments/cancel',
    method: 'post',
    data: { trainingId, userId }
  })
}

export const checkEnrollment = (trainingId, userId) => {
  return request({
    url: '/enrollments/check',
    method: 'get',
    params: { trainingId, userId }
  })
}

export const getUserEnrollments = (userId) => {
  return request({
    url: `/enrollments/user/${userId}`,
    method: 'get'
  })
}

export const getTrainingEnrollments = (trainingId) => {
  return request({
    url: `/enrollments/training/${trainingId}`,
    method: 'get'
  })
}


