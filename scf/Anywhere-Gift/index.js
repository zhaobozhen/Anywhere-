
/**************************************************
Node8.9-Mysql
Reference: mysql api---https://www.npmjs.com/package/mysql
Reference: How to access database---https://cloud.tencent.com/document/product/236/3130
Reference: How to connect api gateway with scf---https://cloud.tencent.com/document/product/628/11983
***************************************************/

function wrapPromise(connection, sql) {
  return new Promise((res, rej) => {
    connection.query(sql, function(error, results, fields) {
      if (error) {
        rej(error)
      } else if (!results.length) {
        res('')
      } else {
        res(results)
      }
    })
  })
}

const crypto = require('crypto') 
function encrypt(data, secretKey){
  var iv = "1234123412341234"

  var cryptoLib = require('@skavinvarnan/cryptlib')

  shaKey = cryptoLib.getHashSha256(secretKey, 32) // This line is not needed on Android or iOS. Its already built into CryptLib.m and CryptLib.java

  var encryptedString = cryptoLib.encrypt(data, secretKey, iv)
  return encryptedString
}

exports.main_handler = async (event, context, callback) => {
  const mysql = require('mysql');
  const querystring = require('querystring');

  const body = event.body
  if (!body) {
      return {
          statusCode : -1,
          msg : 'Request params requested',
          data : ''
      }
  }

  const connection = mysql.createConnection({
    host: '172.16.0.16', // The ip address of cloud database instance, 云数据库实例ip地址
    user: 'root', // The name of cloud database, for example, root, 云数据库用户名，如root
    password: process.env.password, // Password of cloud database, 云数据库密码
    database: 'anywhere' // Name of the cloud database, 数据库名称
  });

  connection.connect()

  const params = querystring.parse(event.body)
  
  const Code = 'code'
  const SSAID = 'ssaid'

  var CodeNum
  var SsiadNum

  for(let i in params) {
    if (i === Code) {
      CodeNum = params[i]
    } else if (i === SSAID) {
      SsiadNum = params[i]
    }
  }

  const updateSql = `UPDATE Gift SET isActive = 1, ${SSAID} = ${SsiadNum} WHERE ${Code} = ${CodeNum}`
  const querySql = `SELECT * from Gift WHERE ${Code} = ${CodeNum}`

  let queryResult = await wrapPromise(connection, querySql)
  
  if (queryResult.length > 0 && queryResult[0].isActive === 0) {
    await wrapPromise(connection, updateSql)
  }
  
  connection.end()

  if (queryResult === '') {
      return {
          statusCode : 1,
          msg : 'No match data',
          data : ''
      }
  }

  let tokenNum = encrypt(SsiadNum, 'absintheeeeeeeeeeeeeeeeeeeeeeeee')

  return {
      statusCode : 0,
      msg : 'Success',
      data : queryResult[0],
      token : tokenNum
  }
}

