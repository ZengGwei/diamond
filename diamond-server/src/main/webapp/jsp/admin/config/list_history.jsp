 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
 <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Diamond配置信息管理历史记录</title>
<script type="text/javascript">
   function confirmForDelete(){
       return window.confirm("你确认要删除该配置信息吗??");  
   }
   function queryConfigInfo(method){
       document.all.queryForm.method.value=method;
       document.all.queryForm.submit();       
   }
  
</script>
</head>
<c:url var="adminUrl" value="/admin.do" >
</c:url>
<c:if test="${method==null}">
    <c:set var="method" value="showHistory"/>
</c:if>

<body>
<c:import url="/jsp/common/message.jsp"/>
<center><h1><strong>历史配置信息管理</strong></h1></center>
   <p align='center'>
   	 <c:if test="${page==null}">
   	 	没有历史记录
   	 </c:if>
     <c:if test="${page!=null}">
      <table border='2' width="90%"  style="border-collapse: collapse" >
          <tr>
          	  <td width="5%">归档时间</td>
              <td width="25%">归档日志</td>
              <td width="20%">dataId</td>
              <td width="10%">组名</td>
              <td width="5%">操作</td>
          </tr>
          <c:forEach items="${page.pageItems}" var="configInfo" varStatus="status">
            <tr <c:if test="${status.index%3 == 0}">style="background-color: CCCCCC"</c:if> >
            
              <td name="tagGmtDate">
                  <fmt:formatDate value="${configInfo.gmtCreate}" pattern="yyyy-MM-dd HH:mm:ss" />
              </td>
              <td name="tagHistoryMemo">
                  <c:out value="${configInfo.memo}" escapeXml="false"/>
              </td>
               <td name="tagDataID">
                  <c:out value="${configInfo.dataId}"/>
               </td>
              <td name="tagGroup">
                  <c:out value="${configInfo.group}" escapeXml="false"/>
              </td>
              
              <c:url var="getConfigInfoHistoryUrl" value="/admin.do" >
                  <c:param name="method" value="detailHistoryConfig" />
              </c:url>
              <c:url var="previewUrl" value="/config.co" >
                  <c:param name="group" value="${configInfo.group}" />
                  <c:param name="dataId" value="${configInfo.dataId}" />
              </c:url>
              <c:url var="showHistoryUrl" value="/admin.do" >
              	  <c:param name="method" value="showHistory" />
                  <c:param name="group" value="${configInfo.group}" />
                  <c:param name="dataId" value="${configInfo.dataId}" />
                  <c:param name="pageSize" value="15" />
                  <c:param name="pageNo" value="1" />
              </c:url>
              <td>
                 <a href="${getConfigInfoHistoryUrl}&id=${configInfo.id}">恢复历史</a>
              </td>
            </tr>
          </c:forEach>
       </table>
       <p align='center'>
          总页数:<c:out value="${page.pagesAvailable}"/>&nbsp;&nbsp;当前页:<c:out value="${page.pageNumber}"/>
          &nbsp;&nbsp;&nbsp;&nbsp;
          <c:url var="nextPage" value="/admin.do" >
             <c:param name="method" value="${method}" />
             <c:param name="group" value="${group}" />
             <c:param name="dataId" value="${dataId}" />
             <c:param name="pageNo" value="${page.pageNumber+1}" />
             <c:param name="pageSize" value="15" />
         </c:url>
         <c:url var="prevPage" value="/admin.do" >
             <c:param name="method" value="${method}" />
             <c:param name="group" value="${group}" />
             <c:param name="dataId" value="${dataId}" />
             <c:param name="pageNo" value="${page.pageNumber-1}" />
             <c:param name="pageSize" value="15" />
         </c:url>
         <c:url var="firstPage" value="/admin.do" >
             <c:param name="method" value="${method}" />
             <c:param name="group" value="${group}" />
             <c:param name="dataId" value="${dataId}" />
             <c:param name="pageNo" value="1" />
             <c:param name="pageSize" value="15" />
         </c:url>
         <c:url var="lastPage" value="/admin.do" >
             <c:param name="method" value="${method}" />
             <c:param name="group" value="${group}" />
             <c:param name="dataId" value="${dataId}" />
             <c:param name="pageNo" value="${page.pagesAvailable}" />
             <c:param name="pageSize" value="15" />
         </c:url>
         <a href="${firstPage}">首页</a>&nbsp;&nbsp; 
          <c:choose>
             <c:when  test="${page.pageNumber==1 && page.pagesAvailable>1}">
               <a href="${nextPage}">下一页</a>  &nbsp; &nbsp;
             </c:when>
             <c:when  test="${page.pageNumber>1 && page.pagesAvailable==page.pageNumber}">
               <a href="${prevPage}">上一页</a>  &nbsp; &nbsp;
             </c:when>
             <c:when  test="${page.pageNumber==1 && page.pagesAvailable==1}">
             </c:when>
             <c:otherwise>
                <a href="${prevPage}">上一页</a>  &nbsp; &nbsp;
                <a href="${nextPage}">下一页</a>  
             </c:otherwise>
          </c:choose>
          <a href="${lastPage}">末页</a>&nbsp;&nbsp; 
       </p>
     </c:if>
  </p>
</body>
</html>