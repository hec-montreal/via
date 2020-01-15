<%@ include file="/templates/includes.jsp"%>
<%@ include file="/templates/header.jsp"%>

<ul class="navIntraTool actionToolBar">
    <li><span><a href="index.htm">${ToolManager.getText("returnbtn")}</a></span></li>
</ul>

<c:if test="${error != null}" >
    <div class="validation"> ${ToolManager.getText(error)}<c:if test="${etext != null}"> - ${etext}</c:if></div><br/><br/> 
</c:if>

<h3>
    <c:out value="${activity.title}"></c:out>
</h3>
<table class="details">
    <c:if test="${activity.description != ''}">
        <tr>
            <td>
                ${ToolManager.getText("description")}
            </td>
            <td style="max-width: 1000px !important; word-wrap: break-word;">
                <c:out value="${activity.description}"></c:out>
            </td>
        </tr>
    </c:if>
     
    <c:if test="${activity.dateBegin != null}">
        <tr>
            <td>
                ${ToolManager.getText("start")}
            </td>
            <td>
                <fmt:setLocale value="${ToolManager.getLocale()}" scope="session"/>
                <fmt:formatDate value="${activity.dateBegin}" pattern="EEEE dd MMMM yyyy, HH:mm"/>
            </td>
        </tr>
        <tr>
            <td>
                ${ToolManager.getText("duration")}
            </td>
            <td>
                <c:out value="${activity.duration}"></c:out>
            </td>
        </tr>
    </c:if>
     
    <tr>
        <td>
            ${ToolManager.getText("lastModification")}
        </td>
        <td>
            <fmt:setLocale value="${ToolManager.getLocale()}" scope="session"/>
            <fmt:formatDate value="${activity.lastModificationDate}" pattern="EEEE dd MMMM yyyy, HH:mm"/>
            <c:out value="${activity.lastModificationUser}"/>
        </td>
    </tr>
     
    <tr>
        <td class="border">
            ${ToolManager.getText("prepare")}
        </td>
        <td class="border">
            <a href="#" class="button btn-primary" onclick='popupwindow("assistance.htm?type=config");'>${ToolManager.getText("assConfig")}</a>
			<a href="#" class="button btn-primary" onclick='popupwindow("assistance.htm?type=assTech");'>${ToolManager.getText("assTech")}</a>
        </td>
    </tr>
    <c:if test="${fn:length(groupList) gt 0}">
        <tr>
            <td class="border" >${ToolManager.getText("assGroups")} </td>
            <td class="border">
            <c:forEach items="${groupList}" var="group" varStatus="loop">
                
                    <c:out value="${group.title}" /> <c:if test="${loop.index != (fn:length(groupList)-1)}">, </c:if>
                
            </c:forEach>
            </td>
        </tr>
    </c:if>
    <tr>
        <td class="border">
            ${ToolManager.getText("assUsers")} 
        </td>
        <td class="border">
            <!-- Presenter - there can only by one -->
            <c:forEach items="${userList}" var="user">
                <c:if test="${user.participantType == 2}">
                    <img src="images/ico_presentor.png" alt="Presentor icon" height="12" width="13"/>
                    ${ToolManager.getText("presentor")}
                    <c:out value="${user.lastName} ,  ${user.firstName}"/>
                    <c:if test="${userCanEdit == true}">
                        (${user.sakaiUserName})  
                    </c:if>
                </c:if>
            </c:forEach>
             
            <br/><br/>
            <!-- list of participants -->
            <c:set var="count" value="0" scope="page"/>
            <table class="users participants">
                <tr>
                    <th>
                        <img src="images/ico_participant.png" alt="Participants icon" height="12" width="13"/>
                        ${ToolManager.getText("participants")}
                    </th>
                </tr>
                 
                <c:forEach items="${userList}" var="user">
                    <c:if test="${user.participantType == 1}">
                        <tr>
                            <td>
                                <c:out value="${user.lastName} ,  ${user.firstName}"/>
                                <c:if test="${userCanEdit == true}">
                                    (${user.sakaiUserName})  
                                </c:if>
                            </td>
                        </tr>
                        <c:set var="count" value="${count + 1}" scope="page"/>
                    </c:if>
                </c:forEach>
                 
                <c:if test="${count == 0}">
                    <tr>
                        <td>
                            ${ToolManager.getText("noParticipants")}
                        </td>
                    </tr>
                </c:if>
            </table>
             
            <!-- list of animators -->
            <c:set var="count" value="0" scope="page"/>
            <table class="users animators">
                <tr>
                    <th>
                        <img src="images/ico_animator.png" alt="Animator icon" height="12" width="13"/>
                        ${ToolManager.getText("animators")}
                    </th>
                </tr>
                 
                <c:forEach items="${userList}" var="user">
                    <c:if test="${user.participantType == 3}">
                        <tr>
                            <td>
                                <c:out value="${user.lastName} ,  ${user.firstName}"/>
                                <c:if test="${userCanEdit == true}">
                                    (${user.sakaiUserName})  
                                </c:if>
                            </td>
                        </tr>
                        <c:set var="count" value="${count + 1}" scope="page"/>
                    </c:if>
                </c:forEach>
                 
                <c:if test="${count == 0}">
                    <tr>
                        <td>
                            ${ToolManager.getText("noAnimators")}
                        </td>
                    </tr>
                </c:if>
            </table>
        </td>
    </tr>
     
    <tr>
        <td class="border">
            ${ToolManager.getText("access")}
        </td>
        <td class="border">
            <c:choose>
                <c:when test="${access == 'true'}">
                    <c:choose>
                    <c:when test="${activity.isPreparation == true}">
                        <c:choose>
                            <c:when test="${activity.participantType == null }">
                                ${ToolManager.getText("noAccess")}
                            </c:when>
                            <c:when test="${activity.participantType == 'Participant'}">
                                ${ToolManager.getText("tooEarly")}
                            </c:when>
                             <c:otherwise>
                                <c:if test="${recording != 0}">
                                    ${ToolManager.getText("recording")}
                                </c:if>
                                <form method="post" action="activity_access.htm" target="_blank" > 
                                    <input type="hidden" name="id" value="${activity.activityID}" />  
                                    
                                    <button class="accessbtn" type="submit"><span class="glyphicon glyphicon-play" aria-hidden="true"></span> ${ToolManager.getText("preperationbtn")}</button>
                                </form>
                            </c:otherwise>
                        </c:choose>
                    </c:when>
                    <c:otherwise>
                            <c:if test="${recording != 0}">
                                ${ToolManager.getText("recording")}
                            </c:if>
                            <form method="post" action="activity_access.htm" target="_blank" > 
                            	<input type="hidden" name="id" value="${activity.activityID}" />  

                                <button class="accessbtn" type="submit"><span class="glyphicon glyphicon-play" aria-hidden="true"></span> ${ToolManager.getText("accessbtn")}</button>
                        	</form>
                    </c:otherwise>
                    </c:choose>
                </c:when>

                <c:when test="${access == 'finished'}">
                    <!-- we add the text saying it's finished -->
                    ${ToolManager.getText("activityFinished")}
                </c:when>
            </c:choose>
        </td>
    </tr>
</table>
<!-- Recordings -->
<c:if test="${fn:length(playbackList) gt 0}">
    <c:set var="count" value="0" scope="page"/>
    <h3>
        ${ToolManager.getText("playbackListTl")}
    </h3>
    <table class="playbackList">
        <c:forEach items="${playbackList}" var="playback">
            <c:if test="${userCanEdit == true || playback.isPublic == 1}">
                <tr class="${playback.isPublic == 0 ? 'grey' : ''}">
                    <td>
                        <c:out value="${playback.title}"/>
                    </td>
                    <td>
                        <fmt:setLocale value="${ToolManager.getLocale()}" scope="session"/>
                        <fmt:formatDate value="${playback.creationDate}" pattern="EEEE dd MMMM yyyy"/>

                        <c:if test="${playback.duration > 3600}">
                            <fmt:formatNumber var="hours" pattern="00" value="${fn:substringBefore((playback.duration div 3600), '.')}"/>
                            <fmt:formatNumber var="minutes" pattern="00" value="${fn:substringBefore(((playback.duration - hours*3600) / 60), '.')}"/>
                            <fmt:formatNumber var="seconds" pattern="00" value="${playback.duration mod 60}"/>
                        </c:if>
                         
                        <c:if test="${playback.duration < 3600}">
                            <c:set var="hours" value="00"/>
                            <c:if test="${playback.duration > 60}">
                                <fmt:formatNumber var="minutes" pattern="00" value="${fn:substringBefore((playback.duration div 60), '.')}"/>
                                <fmt:formatNumber pattern="00" var="seconds" value="${playback.duration mod 60}"/>
                            </c:if>
                            <c:if test="${playback.duration < 60}">
                                <c:set var="minutes" value="00"/>
                                <fmt:formatNumber pattern="00" var="seconds" value="${playback.duration}"/>
                            </c:if>
                        </c:if>
                         
                        ${ToolManager.getText("durationOnly")} <c:out value="${hours}:${minutes}:${seconds}"/>
                    </td>
                    <c:if test="${userCanEdit == true}">
                        <td class="public">
                        ${ToolManager.getText("public")}
                        <form name="formAction" method="post" action="playback_edit.htm" > 
                            <input type="hidden" name="id" value="${activity.activityID}"/>  
                            <input type="hidden" name="playback" value="${playback.playbackID}" /> 
                            <input type="hidden" name="isPublic" value="${playback.isPublic}" /> 
                            <input type="checkbox" class="checkbox" <c:if test="${playback.isPublic == 1}"> checked </c:if> onchange="this.form.submit()" /> 
                        </form>
                        </td>
                        <td>
                            <form name="formAction" method="post" action="playback_edit.htm" > 
                                <input type="hidden" name="id" value="${activity.activityID}"/>  
                                <input type="hidden" name="playback" value="${playback.playbackID}" /> 
                                <input type="hidden" name="title" value="${playback.title}" />  
                                <input type="submit" class="edit" value="${ToolManager.getText("editPlayback")}" />
                            </form>
                        
                        </td>
                        
                            <td>
                                <c:if test="${playback.hasFullVideoRecord == 1}">
                                    <form name="formAction" method="post" action="record_download.htm" style="text-align:center;"> 
                                        <input type="hidden" name="playback" value="${playback.playbackID}" />
                                        <input type="hidden" name="recordType" value="1" />
                                        <img src="images/ico_fullvideo.png"/><br/>
                                        <input type="submit" class="edit" value="${ToolManager.getText("dlFullVideo")}" />
                                    </form>
                                </c:if>
                            </td>
                        
                            <td>
                                <c:if test="${playback.hasMobileVideoRecord == 1}">
                                    <form name="formAction" method="post" action="record_download.htm" style="text-align:center;"> 
                                        <input type="hidden" name="playback" value="${playback.playbackID}" />
                                        <input type="hidden" name="recordType" value="2" />
                                        <img src="images/ico_mobile.png"/><br/>
                                        <input type="submit" class="edit" value="${ToolManager.getText("dlMobileVideo")}" />
                                    </form>
                                </c:if>
                            </td>
                        
                            <td>
                                <c:if test="${playback.hasAudioRecord == 1}">
                                    <form name="formAction" method="post" action="record_download.htm" style="text-align:center;"> 
                                        <input type="hidden" name="playback" value="${playback.playbackID}" />
                                        <input type="hidden" name="recordType" value="3" />
                                        <img src="images/ico_audio.png"/><br/>
                                        <input type="submit" class="edit" value="${ToolManager.getText("dlAudio")}" />
                                    </form>
                                </c:if>
                            </td>
                    </c:if>
                    <td>
                        <form method="post" action="activity_access.htm" target="_blank">
                            <input type="hidden" name="id" value="${activity.activityID}"/>
                            <input type="hidden" name="playback" value="${playback.playbackID}"/>
                            <img src="images/access.png" height="12" width="11" class="accessbtn"/>
                            <input type="submit" value="${ToolManager.getText("viewbtn")}" class="accessbtn"/>
                        </form>
                    </td>
                </tr>
                <c:set var="count" value="${count + 1}" scope="page"/>
                <!-- we display breakouts if there are any -->
               
                <c:if test="${fn:length(playback.breackOutPlaybackList) gt 0}">
                 
                    <c:forEach items="${playback.breackOutPlaybackList}" var="breakout">
                        <c:if test="${userCanEdit == true || breakout.isPublic == 1}">
                        <tr class="${playback.isPublic == 0 ? 'breakout grey' : 'breakout'}">
                                <td>
                                    <img src="images/arrow.png" height="16" width="16" class="breakout"/><span class="breakoutTL"><c:out value="${breakout.title}"/></span>
                                </td>
                            <td>
                                <fmt:setLocale value="${ToolManager.getLocale()}" scope="session"/>
                                <fmt:formatDate value="${breakout.creationDate}" pattern="EEEE dd MMMM yyyy"/>

                                <c:if test="${breakout.duration > 3600}">
                            <fmt:formatNumber var="hours" pattern="00" value="${fn:substringBefore((breakout.duration div 3600), '.')}"/>
                            <fmt:formatNumber var="minutes" pattern="00" value="${fn:substringBefore(((breakout.duration - hours*3600) / 60), '.')}"/>
                            <fmt:formatNumber var="seconds" pattern="00" value="${breakout.duration mod 60}"/>
                                </c:if>
                                 
                                <c:if test="${breakout.duration < 3600}">
                                    <c:set var="hours" value="00"/>
                                    <c:if test="${breakout.duration > 60}">
                                <fmt:formatNumber var="minutes" pattern="00" value="${fn:substringBefore((breakout.duration div 60), '.')}"/>
                                <fmt:formatNumber pattern="00" var="seconds" value="${breakout.duration mod 60}"/>
                                    </c:if>
                                    <c:if test="${breakout.duration < 60}">
                                        <c:set var="minutes" value="00"/>
                                        <fmt:formatNumber pattern="00" var="seconds" value="${breakout.duration}"/>
                                    </c:if>
                                </c:if>
                                 
                                ${ToolManager.getText("durationOnly")} <c:out value="${hours}:${minutes}:${seconds}"/>
                            </td>
                            <c:if test="${userCanEdit == true}">
                                <td class="public">
                                ${ToolManager.getText("public")}
                                    <form name="formAction" method="post" action="playback_edit.htm" > 
                                        <input type="hidden" name="id" value="${activity.activityID}"/>  
                                        <input type="hidden" name="playback" value="${breakout.playbackID}" /> 
                                        <input type="hidden" name="isPublic" value="${breakout.isPublic}" /> 
                                        <input type="checkbox" class="checkbox" <c:if test="${breakout.isPublic == 1}"> checked </c:if> onchange="this.form.submit()" /> 
                                    </form>
                                </td>
                                <td>
                                    <form name="formAction" method="post" action="playback_edit.htm" > 
                                        <input type="hidden" name="id" value="${activity.activityID}"/>  
                                        <input type="hidden" name="playback" value="${breakout.playbackID}" /> 
                                        <input type="hidden" name="title" value="${breakout.title}" />  
                                        <input type="submit" class="edit" value="${ToolManager.getText("editPlayback")}" />
                                    </form>
                                </td>
                                
                                    <td>
                                         <c:if test="${breakout.hasFullVideoRecord == 1}">
                                            <form name="formAction" method="post" action="record_download.htm" style="text-align:center;"> 
                                                <input type="hidden" name="playback" value="${breakout.playbackID}" />
                                                <input type="hidden" name="recordType" value="1" />
                                                <img src="images/ico_fullvideo.png"/><br/>
                                                <input type="submit" class="edit" value="${ToolManager.getText("dlFullVideo")}" />
                                            </form>
                                        </c:if>
                                    </td>
                                
                                    <td>
                                        <c:if test="${breakout.hasMobileVideoRecord == 1}">
                                            <form name="formAction" method="post" action="record_download.htm" style="text-align:center;"> 
                                                <input type="hidden" name="playback" value="${breakout.playbackID}" />
                                                <input type="hidden" name="recordType" value="2" />
                                                <img src="images/ico_mobile.png"/><br/>
                                                <input type="submit" class="edit" value="${ToolManager.getText("dlMobileVideo")}" />
                                            </form>
                                        </c:if>
                                    </td>
                                
                                    <td>
                                        <c:if test="${breakout.hasAudioRecord == 1}">
                                            <form name="formAction" method="post" action="record_download.htm" style="text-align:center;"> 
                                                <input type="hidden" name="playback" value="${breakout.playbackID}" />
                                                <input type="hidden" name="recordType" value="3" />
                                                <img src="images/ico_audio.png"/><br/>
                                                <input type="submit" class="edit" value="${ToolManager.getText("dlAudio")}" />
                                            </form>
                                        </c:if>
                                    </td>
                            </c:if>
                            <td>
                                <form method="post" action="activity_access.htm" target="_blank">
                                    <input type="hidden" name="id" value="${activity.activityID}"/>
                                    <input type="hidden" name="playback" value="${breakout.playbackID}"/>
                                    <img src="images/access.png" height="12" width="11" class="accessbtn"/>
                                    <input type="submit" value="${ToolManager.getText("viewbtn")}" class="accessbtn"/>
                                </form>
                            </td>
                            </tr>
                        </c:if>
                    </c:forEach>
                </c:if>
            </c:if>
        </c:forEach>
         
        <c:if test="${count == 0}">
            <tr>
                <td>
                    ${ToolManager.getText("noPublicRecordings")}
                </td>
            </tr>
        </c:if>
    </table>
</c:if>
<!--<ul class="navIntraTool actionToolBar">
    <li><span><a href="index.htm">${ToolManager.getText("returnbtn")}</a></span></li>
</ul>-->
<script type="text/javascript">
function popupwindow(url) {window.open(url, '', 'toolbar = no, location = no, directories = no, status = no, menubar = no, scrollbars = no, resizable = yes, copyhistory = no, width = 815, height = 700, top = 100, left = 200');}
</script>
<%@ include file="/templates/footer.jsp"%>