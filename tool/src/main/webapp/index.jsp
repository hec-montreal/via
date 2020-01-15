<%@ include file="/templates/includes.jsp" %>
<%@ include file="/templates/header.jsp" %>

<ul class="navIntraTool actionToolBar">
    <c:if test="${userCanCreate ==  true}">
          <li class="firstToolBarItem"><span><a href="activity_new.htm">${ToolManager.getText("newActivitybtn")}</a></span></li>
    </c:if>
</ul>

<c:if test="${error != null}" >
    <div class="validation"> ${ToolManager.getText(error)}<c:if test="${etext != null}"> - ${etext}</c:if></div><br/><br/> 
</c:if>

<c:if test="${userCanCreate ==  true && fn:length(groupList) gt 0}">
    <form id="cal" name="form" method="post" action="index.htm" >
        <span>${ToolManager.getText("GroupFilter")}</span>

        <select id="group" name="group" onchange="this.form.submit()">
	        <option value=""></option>
	        <c:if test="${canEditActivityOfSite ==  true}">
               <option value="site" <c:if test="${'site' == groupSelected}"> selected </c:if> >${ToolManager.getText("site")}</option>
            </c:if>
	        <c:forEach items="${groupList}" var="group">
	            <option value="${group.id}" <c:if test="${group.id == groupSelected}"> selected </c:if> > ${group.title}</option> 
	        </c:forEach>
        </select>
    </form>
</c:if>

<table class="activitylist">
<tr class="title">
    <td>${ToolManager.getText("myActivityListTl")}</td>
</tr>
 <tr class="header">
      <td style="width:20%">${ToolManager.getText("action")}</td>
      <td style="width:30%">${ToolManager.getText("activityName")}</td>
      <td style="width:20%">${ToolManager.getText("activityDate")}</td>
      <td style="width:15%">${ToolManager.getText("activityStatus")}</td>
      <td style="width:15%">${ToolManager.getText("activityGroup")}</td>
 </tr>
<c:set var="count" value="0" scope="page"/>
<c:forEach items="${activityList}" var="activity">
    <c:if test="${activity.key.activityType == 'Normale'}">
      <tr class="activity">
            <c:if test="${activity.value == true}">
                <td>
                    <form name="formAction" method="post" action="index.htm" > 
                        <input type="hidden" name="id" value="${activity.key.activityID}" />
                        <select class="activityActions" name="actions" onchange="this.form.submit()">        
                            <option value="0">${ToolManager.getText("select")}</option>
                            <option value="1">${ToolManager.getText("modify")}</option>
                            <option value="2">${ToolManager.getText("delete")}</option>
                        </select>
                    </form>
                </td>
            </c:if>
            <c:if test="${activity.value !=  true}">
                <td>
                </td>
            </c:if>
            <td>
                <form method="post" action="activity_details.htm" > 
                    <input type="hidden" name="id" value="${activity.key.activityID}" />
                    <img src="images/ico.png" height="16" width="11"/><input type="submit" value="${activity.key.title}" class="activityname">
                </form>
            </td>
            <td> 
            
            <span class="grey"><fmt:setLocale value="${ToolManager.getLocale()}" scope="session"/><fmt:formatDate value="${activity.key.dateBegin}" pattern="EEEE dd MMMM, HH:mm" /> ${ToolManager.getText("to")}
            <fmt:formatDate value="${activity.key.dateEnd}" pattern=" HH:mm" /></span>
            </td>
            <td class="grey" style="line-height:20px !important;">
            <c:choose>
            <c:when test="${now < activity.key.dateEnd}">
                <c:choose>
                <c:when test="${activity.key.isPreparation == true}">
                    <c:choose>
                        <c:when test="${activity.key.participantType == null }">
                             ${ToolManager.getText("noAccess")}
                        </c:when>
                        <c:when test="${activity.key.participantType == 'Participant'}">
                            ${ToolManager.getText("tooEarlyShort")}
                        </c:when>
                         <c:otherwise>
                            <form method="post" action="activity_access.htm" target="_blank" > 
                                <input type="hidden" name="id" value="${activity.key.activityID}" />
                                <img src="images/access_blue.png" height="12" width="11"/><input type="submit" value="${ToolManager.getText("preperationbtn")}" class="access" />  
                            </form>
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:otherwise>
                    <form method="post" action="activity_access.htm" target="_blank" > 
                        <input type="hidden" name="id" value="${activity.key.activityID}" />
                        <img src="images/access_blue.png" height="12" width="11"/><input type="submit" value="${ToolManager.getText("accessbtn")}" class="access" />  
                    </form>
                </c:otherwise>
                </c:choose>
            </c:when>
            <c:otherwise>
                <span class="grey">${ToolManager.getText("finished")}</span>
            </c:otherwise>
            </c:choose>
            </td>
            <td>
                <c:if test="${activity.key.getLstGroup() !=  null}">
                    <c:forEach items="${activity.key.getLstGroup()}" var="activityGroup">
                        <c:forEach items="${groupList}" var="group">
                            <c:if test="${group.id == activityGroup}">
                                <span class="grey"> ${group.title} </span>
                            </c:if>
                        </c:forEach>
                    </c:forEach>
                </c:if>
            </td>
        </tr>
        <c:set var="count" value="${count + 1}" scope="page"/>
    </c:if>
</c:forEach>

<c:if test="${count == 0}">
<tr><td>${ToolManager.getText("noActivities")}</td></tr>
</c:if>
</table>

<table class="activitylist" >
 <tr class="title"><td> ${ToolManager.getText("myPermActivityListTl")} </td></tr>
   <tr class="header">
      <td style="width:20%">${ToolManager.getText("action")}</td>
      <td style="width:30%">${ToolManager.getText("activityName")}</td>
      <td style="width:20%">${ToolManager.getText("activityDate")}</td>
      <td style="width:15%">${ToolManager.getText("activityStatus")}</td>
      <td style="width:15%">${ToolManager.getText("activityGroup")}</td>
   </tr>
<c:set var="count" value="0" scope="page"/>
<c:forEach items="${activityList}" var="activity">
    <c:if test="${activity.key.activityType == 'Permanente'}">
       <tr class="activity">
             <c:if test="${activity.value ==  true}">
                <td>
                    <form name="formAction" method="post" action="index.htm" > 
                        <input type="hidden" name="id" value="${activity.key.activityID}" />
                        <select class="activityActions" id="actions" name="actions" onchange="this.form.submit()">        
                            <option value="0">${ToolManager.getText("select")}</option>
                            <option value="1">${ToolManager.getText("modify")}</option>
                            <option value="2">${ToolManager.getText("delete")}</option>
                        </select>
                    </form>
                </td>
            </c:if>
            <c:if test="${activity.value !=  true}">
                <td>
                </td>
            </c:if>
            <td>
                <form method="post" action="activity_details.htm" > 
                    <input type="hidden" name="id" value="${activity.key.activityID}" />
                    <img src="images/ico.png" height="16" width="11"/><input type="submit" value="${activity.key.title}" class="activityname" />
                </form>
            </td>
            <td></td>
            <td>
                <form method="post" action="activity_access.htm" target="_blank" > 
                    <input type="hidden" name="id" value="${activity.key.activityID}" />
                    <img src="images/access_blue.png" height="12" width="11"/><input type="submit" value="${ToolManager.getText("accessbtn")}" class="access" />  
                </form>
            </td>
              <td>
                  <c:if test="${activity.key.getLstGroup() !=  null}">
                      <c:forEach items="${activity.key.getLstGroup()}" var="activityGroup">
                          <c:forEach items="${groupList}" var="group">
                              <c:if test="${group.id == activityGroup}">
                                  <span class="grey"> ${group.title} </span>
                              </c:if>
                          </c:forEach>
                      </c:forEach>
                  </c:if>
              </td>
       </tr>
        <c:set var="count" value="${count + 1}" scope="page"/>
    </c:if>
</c:forEach>
<c:if test="${count == 0}">
<tr><td>${ToolManager.getText("noActivities")}</td></tr>
</c:if>
</table>

<script type="text/javascript">
    $('#month option[value="' + ${month_value} + '"]').prop('selected', true);
    $('#year option[value="' + ${year_value} + '"]').prop('selected', true);
    
    $(document).ready(function(){
        $('#actions option[value="0"]').prop('selected', true);
    });

</script>



