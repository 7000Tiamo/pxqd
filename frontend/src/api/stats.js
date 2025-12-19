import request from './request'

export const getOverviewStats = () => {
  return request({
    url: '/stats/overview',
    method: 'get'
  })
}

// 全局统计（数据统计页概览）
export const getTongji = () => {
  return request({
    url: '/stats/tongji',
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

// 导出：按培训项目统计（Excel）
export const exportStatsByTraining = () => {
  return request({
    url: '/stats/by-training/export',
    method: 'get',
    responseType: 'blob'
  })
}

// 导出：按部门统计（Excel）
export const exportStatsByDepartment = () => {
  return request({
    url: '/stats/by-department/export',
    method: 'get',
    responseType: 'blob'
  })
}

// 导出：按员工统计（Excel）
export const exportStatsByEmployee = () => {
  return request({
    url: '/stats/by-employee/export',
    method: 'get',
    responseType: 'blob'
  })
}


