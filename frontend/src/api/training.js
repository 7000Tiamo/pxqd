import request from './request'

export const getTrainingList = (params) => {
  return request({
    url: '/trainings',
    method: 'get',
    params
  })
}

export const getTrainingDetail = (id) => {
  return request({
    url: `/trainings/${id}`,
    method: 'get'
  })
}

export const createTraining = (data) => {
  return request({
    url: '/trainings',
    method: 'post',
    data
  })
}

export const updateTraining = (id, data) => {
  return request({
    url: `/trainings/${id}`,
    method: 'put',
    data
  })
}

export const publishTraining = (id) => {
  return request({
    url: `/trainings/${id}/publish`,
    method: 'post'
  })
}

export const deleteTraining = (id) => {
  return request({
    url: `/trainings/${id}`,
    method: 'delete'
  })
}

// 公开获取培训基本信息（用于扫码签到/签退页面）
export const getPublicTrainingInfo = (id) => {
  return request({
    url: `/trainings/public/${id}`,
    method: 'get'
  })
}


