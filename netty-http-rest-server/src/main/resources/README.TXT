归属地服务

部署相关修改处：
1.监听端口：
application.yml中的netty项下的port
2.数据库配置：
application.yml中的jdbc项下所有项

接口定义：
访问地址：http:\\{服务器地址}:{在cofig文件夹中netty:port配置的端口}
1.归属地服务在请求体中写入以下json
{
  "request": 
  {
	"common":
	{
		"action" : "regionarea",
		"reqtime": "当前时间戳"
	},
	"content": 
	{
		"phoneNo": "手机号码"
	}
  }
}

2.短信服务在请求体中写入以下json
{
  "request": 
  {
	"common":
	{
		"action" : "sms",
		"asyn"   : "true为异步，false为同步",
		"reqtime": "当前时间戳"
	},
	"content": 
	{
		"phoneNo": "手机号码",
        "message": "短信内容",
        "num"    : "通道编号，如果新增通道时请把通道配置相关信息告诉实施人员，实施人员在sms.properties文件中顺序增加改通道配置，并把标号告诉使用人员",
	}
  }
}

3.邮件服务在请求体中写入以下json
{
  "request": 
  {
	"common":
	{
		"action": "email",
		"asyn"   : "true为异步，false为同步",
		"reqtime": "当前时间戳"
	},
	"content": 
	{
        "emailTo": "发送人地址",
        "subject": "标题",
        "message": "邮件正文"
	}
  }
}

启动相关：
windows:双击目录下startup.bat文件进行启动。
Linux：执行startup.sh

停止相关：
Linux: 执行stop.sh