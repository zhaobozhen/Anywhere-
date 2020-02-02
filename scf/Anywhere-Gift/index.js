
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
      }
      res(results)
    })
  })
}


exports.main_handler = async (event, context, callback) => {
  const mysql = require('mysql');

  const body = event.body
  if (!body) {
      return {
          statusCode : -1,
          msg : 'Request params requested'
      }
  }

  const connection = mysql.createConnection({
    host: '172.16.0.16', // The ip address of cloud database instance, 云数据库实例ip地址
    user: 'root', // The name of cloud database, for example, root, 云数据库用户名，如root
    password: process.env.password, // Password of cloud database, 云数据库密码
    database: 'anywhere' // Name of the cloud database, 数据库名称
  });

  connection.connect();

  // get value from apigw
  const {CustomerID, CustomerName} = event.queryString

  const updateSql = `UPDATE Customers SET CustomerName = '${CustomerName}' WHERE CustomerID = ${CustomerID}`
  const querySql = `SELECT * from Gift WHERE code='` + event.body.split("=")[1] + "'"

  let queryResult = await wrapPromise(connection, querySql)
  
  connection.end();

  if (queryResult === '[\n]') {
      return {
          statusCode : 1,
          msg : 'No match data'
      }
  }
  return queryResult
}

