<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link type="text/css" rel="stylesheet" href="/angularjs/css/bootstrap.min.css" />
<script type="text/javascript" language="javascript" src="/angularjs/js/angular.js"></script>
</head>
<body ng-app>
<div ng-controller="weatherController">
	<input type="text" ng-model="cityname" />
	<p>
		{{weather.city}} //城市
		{{weather.pinyin}} //城市拼音
		{{weather.citycode}}  //城市编码	
		{{weather.date}} //日期
		{{weather.time}} //发布时间
		{{weather.postCode}} //邮编
		{{weather.longitude}}, //经度
		{{weather.latitude}}, //维度
		{{weather.altitude}} //海拔	
		{{weather.weather}}  //天气情况
		{{weather.temp}} //气温
		{{weather.l_tmp}} //最低气温
		{{weather.h_tmp}} //最高气温
		{{weather.WD}}	 //风向
		{{weather.WS}} //风力
		{{weather.sunrise}} //日出时间
		{{weather.sunset}} //日落时间
	</p>
	<p>{{test}}</p>
</div>
<script type="text/javascript">
function weatherController($http,$scope){
	$scope.cityname = '上海';
	$http.jsonp('http://apistore.baidu.com/microservice/weather?cityname=' + $scope.cityname).success(function(rs){
		$scope.test=rs;
		$scope.weather = rs.retData;
	});
}
</script>
</body>
</html>