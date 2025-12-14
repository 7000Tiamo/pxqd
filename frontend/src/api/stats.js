import request from './request'

export const getOverviewStats = () => {
  return request({
    url: '/stats/overview',
    method: 'get'
  })
}

export const getStatsByTraining = () => {
  return request({
    url: '/stats/by-training',
    method: 'get'
  })
}

export const getStatsByDepartment = () => {
  return request({
    url: '/stats/by-department',
    method: 'get'
  })
}

export const getStatsByEmployee = () => {
  return request({
    url: '/stats/by-employee',
    method: 'get'
  })
}


