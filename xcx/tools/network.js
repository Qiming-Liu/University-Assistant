function getrequest(url, params, success, fail) {
  wx.request({
    url: 'https://www.jhuncloud.com' + url,
    data: params,
    header: {
      'content-type': 'application/x-www-form-urlencoded'
    },
    method: 'get',
    success: function (res) {
      if (res.statusCode == 200) {
        success(res.data)
      } else {
        fail()
      }
    },
    fail: function (res) {
      fail()
    },
    complete: function (res) {
    },
  })
}
module.exports = {
  getrequest: getrequest
}