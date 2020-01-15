<%@ include file="/templates/includes.jsp" %>
<%@ include file="/templates/header.jsp" %>
<%
    request.setCharacterEncoding("UTF-8");
%>

<c:if test="${error != null}" >
    <div class="validation"> ${ToolManager.getText(error)}<c:if test="${etext != null}"> - ${etext}</c:if></div><br/><br/> 
</c:if>

<c:if test="${modify != null}">
    <h3> ${ToolManager.getText("modifyActivity")} </h3>
</c:if>
<c:if test="${modify == null}">
    <h3> ${ToolManager.getText("newActivitybtn")} </h3>
</c:if>

<p id="warning" class="validation hide">${ToolManager.getText("warning")}</p>

<form id="form" name="activityForm" action="" method="POST" class="edit_activity">

<!-- these affect what can be modified -->
<input type="text" style="display:none;" name="id" value="${id}" />
<input type="text" style="display:none;" name="canModify" id="canModifyAll" value="${canModifyAll}" /> 
<input type="text" style="display:none;" name="canModifyRoomType" id="canModifyRoomType" value="${canModifyRoomType}" /> 
<!--  -->

<label class="label" id=lblTitle for="title">${ToolManager.getText("title")}</label>
<input type="text" name="title" id="title" value="${title}" maxlength="100" size="50" autofocus />&nbsp;* ${ToolManager.getText("newTitle")}<br/><br/><!-- validate max legth in Via -->

<label class="label" for="description">${ToolManager.getText("description")}</label>
<textarea name="description" id="description" rows="10" cols="75"  maxlength="250" >${description}</textarea><div style="padding-left: 155px;">* ${ToolManager.getText("newDescription")}</div><br/><br/>

<label class="label" for="date">${ToolManager.getText("date")}</label>
<input type="text" name="date" size="15" id="startDatePicker" value="${startDate}" readonly="readonly" />

<label class="label" id="lblStartTime" for="startTime">${ToolManager.getText("startTime")}</label>
<input type="text" name="startTime" maxlength="5" size="5" id="startTime" value="${startTime}"/> *
<input type="checkbox" name="permanent" id="permanent"  <c:if test="${activityType == 2}"> checked </c:if> /><label style="padding-top: 5px;" class="label" for="permanent"> ${ToolManager.getText("permanent")}</label> <br/><br/>

<label class="label" id="lblDuration"  for="duration">${ToolManager.getText("duration")}</label>
<input type="text" id="duration" name="duration" value="${duration}" maxlength="3" /> * ${ToolManager.getText("newDuration")}<br/><br/>

<c:if test="${canEditActivityOfSite ==  true}">
    <label class="label" for="enrollmentType">${ToolManager.getText("userLabel")}</label>
    <input type="radio" name="enrollmentType" id="site" value="0" <c:if test="${enrollmentType == 0}">checked</c:if> ><label class="sublabel" for="site">${ToolManager.getText      ("automaticEnrollment")}</label><br />
</c:if>
<label class="label" for="enrollmentType"></label>
<input type="radio" name="enrollmentType" id="manual" value="1" <c:if test="${enrollmentType == 1}">checked</c:if> ><label for="manual" class="sublabel">${ToolManager.getText("manualEnrollment")}</label><br />
<label class="label" for="enrollmentType"></label>
<input type="radio" name="enrollmentType" id="group" value="2" <c:if test="${enrollmentType == 2}">checked</c:if> ><label for="group" class="sublabel">${ToolManager.getText("groupEnrollment")}</label>

    <!-- group list  -->
    <div id="groupEnrollment">
        <c:forEach items="${groupList}" var="group">
            <input id="chk_ge_${group.group.id}" type="checkbox" class="groups" name="groups" value="${group.group.id}" <c:if test="${group.isSelected}"> checked </c:if> /> <label for="chk_ge_${group.group.id}" class="sublabel">${group.group.title}</label> <br/>
        </c:forEach>
    </div> 
<!-- end of group list -->

<!-- User tables -->
    <div id="automaticEnrollment">
        <!-- Presenter - there can only by one -->
        <div>
        	<img src="images/ico_presentor.png" alt="Presentor icon" style="float: left; padding-top: 3px; padding-right: 5px;"/>
        	<div style="float: left;">${ToolManager.getText("presentor")}</div>
        	
	         <c:choose>
	            <c:when test="${presentorID == null && savedUserList == null}" >
	                <div class="divPresenterArrow border" style="margin-top: -7px;"></div>
	            </c:when>
	            <c:otherwise>
	                 <div class="divPresenterArrow" style="margin-top: -7px;"></div>
	            </c:otherwise>
	        </c:choose>
	        
	        <div class="divUnclickable">.</div>
	        
			<select id="presentor" name="presentor" size="1" style="margin-top: -10px;"> 
                <c:choose>
                    <c:when test="${presentorID != null}">
                        <option value="${presentorID}" >${presentor.lastName}, ${presentor.firstName}  (${presentor.eid})</option> 
                    </c:when>
                    <c:when test="${savedUserList != null}">
                        <c:forEach items="${savedUserList}" var="user">
                            <c:if test="${user.participantType == 2}"> 
                                <option value="${user.sakaiUserID}" >${user.lastName}, ${user.firstName} (${user.sakaiUserName})</option>  
                            </c:if>
                        </c:forEach>      
                    </c:when>
                </c:choose>
            </select>	        
        </div>

        <br/>
        
        <span class="grey">${ToolManager.getText("selectPresentor")}  </span>  
        <br/>
        <br/>
        <div class="userAssContainer">
        
            <div id="divMembers">
                <div class="users members">
                    ${ToolManager.getText("members")}<br/>
                    
                    <select id="members" name="members" size="20"  multiple="multiple">
                        <c:if test="${userList != null && enrollmentType == 1}">
                            <c:forEach items="${userList}" var="user"> 
                                <c:if test="${user.id != presentorID}">
                                    <option value="${user.id}" title="${user.lastName}, ${user.firstName} (${user.eid})">${user.lastName}, ${user.firstName} (${user.eid})</option>   
                                </c:if>
                            </c:forEach>
                        </c:if>
                    </select>
                    <br/>
                    <br/>
                    <span class="grey">${ToolManager.getText("search")} </span><input type="text" id="members" style="width:220px;" onkeyup="filterParticipantsList(this);"/>
                   
                </div> 
                
                <div class="tools" id="toolsMembers">
                    <a href="#" id="addMToP" class="tools">&gt;</a>  
                    <br/>
                    <a href="#" id="removeMToP" class="tools">&lt;</a>
                    <br/>
                    <br/>
                    <br/>
                    <a href="#" id="addAllMToP" class="tools">&gt;&gt;</a>  
                    <br/>
                    <a href="#" id="removeAllMToP" class="tools">&lt;&lt;</a>
                </div>
            </div>
            
            <!-- list of participants -->  
            <div class="users participants">
                <img src="images/ico_participant.png" alt="Participants icon" height="12" width="13"/>
                ${ToolManager.getText("participants")}<br/>
                
                <select id="participants" name="participants" size="20"  multiple="multiple">
                <c:choose>
                    <c:when test="${savedUserList != null }">
                        <c:forEach items="${savedUserList}" var="user">
                            <c:if test="${user.participantType == 1}">
                                <option value="${user.sakaiUserID}"  title="${user.lastName}, ${user.firstName} (${user.sakaiUserName})" >${user.lastName}, ${user.firstName} (${user.sakaiUserName})</option>  
                            </c:if>
                        </c:forEach>      
                    </c:when>   
                   
                   <c:when test="${userList != null && enrollmentType != 1}">
                        <c:forEach items="${userList}" var="user"> 
                            <c:if test="${user.id != presentorID}">
                                <option value="${user.id}" title="${user.lastName}, ${user.firstName} (${user.eid})" >${user.lastName}, ${user.firstName} (${user.eid})</option>   
                            </c:if>
                        </c:forEach>
                    </c:when>
                   
                  </c:choose>  
                </select>
                    
                <br/>
                <br/>
                <span class="grey">${ToolManager.getText("search")} </span><input type="text" id="participants" style="width:220px;" onkeyup="filterParticipantsList(this);"/>
                <br/>
                <br/>
                <a href="#" id="addPresentor">${ToolManager.getText("newPresentor")}</a>
               
            </div> 
            
            <div class="tools">
                <a href="#" id="addPToA" class="tools">&gt;</a>  
                <br/>
                <a href="#" id="removePToA" class="tools">&lt;</a>
                <br/>
                <br/>
                <br/>
                <a href="#" id="addAllPToA" class="tools">&gt;&gt;</a>  
                <br/>
                <a href="#" id="removeAllPToA" class="tools">&lt;&lt;</a>
            </div>
    
            <!-- list of animators -->
            <div class="users animators">
                <img src="images/ico_animator.png" alt="Animator icon" height="12" width="13"/>
                ${ToolManager.getText("animators")}<br/>
                <select id="animators" name="animators" size="20" multiple="multiple">
                     <!-- New activity -->
                    <c:if test="${tempAnimators != null}">
                        <c:forEach items="${tempAnimators}" var="animator"> 
                            <c:if test="${animator.id != presentorID}">
                                <option value="${animator.id}" title="${animator.lastName}, ${animator.firstName} (${animator.eid})" >${animator.lastName}, ${animator.firstName} (${animator.eid})</option>   
                            </c:if>
                        </c:forEach>
                    </c:if>
                    <!-- Activity modification -->
                    <c:if test="${savedUserList != null}">
                        <c:forEach items="${savedUserList}" var="user">
                            <c:if test="${user.participantType == 3}">
                                <option value="${user.sakaiUserID}" title="${user.lastName}, ${user.firstName} (${user.sakaiUserName})">${user.lastName}, ${user.firstName} (${user.sakaiUserName})</option>  
                            </c:if>
                        </c:forEach>      
                    </c:if>   
                </select>
                
            </div>
        </div>
    </div>

<br /><br /><br /><br /><br /><br /><br /><br />
<label class="label" for="chkSendInvitation">${ToolManager.getText("lblSendInvitation")}</label>
<input type="checkbox" id="chkSendInvitation" name="chkSendInvitation" value="true" /><br /><br/>

<label class="label" for="ddlReminder">${ToolManager.getText("lblReminder")}</label>
<select id="ddlReminder" name="ddlReminder">
    <option value="0">${ToolManager.getText("reminder0")}</option>
    <option value="1" <c:if test="${savedReminder == 1}"> selected </c:if> >${ToolManager.getText("reminder1")}</option>
    <option value="2" <c:if test="${savedReminder == 2}"> selected </c:if> >${ToolManager.getText("reminder2")}</option>
    <option value="3" <c:if test="${savedReminder == 3}"> selected </c:if> >${ToolManager.getText("reminder3")}</option>
    <option value="4" <c:if test="${savedReminder == 4}"> selected </c:if> >${ToolManager.getText("reminder4")}</option>
    <option value="5" <c:if test="${savedReminder == 5}"> selected </c:if> >${ToolManager.getText("reminder5")}</option>
</select><br/><br/>

<label class="label"  for="roomType">${ToolManager.getText("type")}</label>
<select id="roomType" name="roomType" >
    <option value="1">${ToolManager.getText("standarda")}</option>
    <option value="2" <c:if test="${roomType == 2}"> selected </c:if> >${ToolManager.getText("seminaire")}</option>
</select><br/><br/>

<label class="label" for="mediaQuality">${ToolManager.getText("mediaQuality")}</label>
<select id="mediaQuality" name="mediaQuality" >
    <c:forEach items="${mediaQuality}" var="media" >
        <option value="${media.profilID}" <c:if test="${savedMedia == media.profilID}"> selected </c:if> >${media.profilName}</option>
    </c:forEach>
</select><br/><br/>

<label class="label" for="recordingMode">${ToolManager.getText("recordingMode")}</label>
<select id="recordingMode" name="recordingMode" >
    <option value="2" >${ToolManager.getText("multiple")}</option>
    <option value="0" <c:if test="${savedMode == 0}"> selected </c:if> >${ToolManager.getText("deactivated")}</option>
    <option value="1" <c:if test="${savedMode == 1}"> selected </c:if> >${ToolManager.getText("unified")}</option>   
</select><br/><br/>

<label class="label" for="recordingBehavior">${ToolManager.getText("recordingBehavior")}</label>
<select id="recordingBehavior" name="recordingBehavior" >
    <option value="1">${ToolManager.getText("automatic")}</option>
    <option value="2" <c:if test="${savedBehavior == 2}"> selected </c:if> >${ToolManager.getText("manual")}</option>
</select><br/><br/>

<label class="label" for="recordingIsPublic">${ToolManager.getText("recordingPublic")}</label>
<select id="recordingIsPublic" name="recordingIsPublic" >
    <option value="true" >${ToolManager.getText("yes")}</option>
    <option value="false" <c:if test="${savedIsPublic == false}"> selected </c:if>>${ToolManager.getText("no")}</option>
</select><br/><br/>

<label class="label" for="waitingRoom">${ToolManager.getText("waitingRoom")}</label>
<select id="waitingRoom" name="waitingRoom" >
    <option value="0">${ToolManager.getText("none")}</option>
    <option value="1" <c:if test="${savedWR == 1}"> selected </c:if> >${ToolManager.getText("authorisation")}</option>
    <option value="2" <c:if test="${savedWR == 2}"> selected </c:if> >${ToolManager.getText("presenter")}</option>
</select><br/><br/>

<input id="btnSave" type="button" onclick="validateBeforeSubmit()" value="${ToolManager.getText("save")}" />

<a class="button btn-primary" id="btnCancel" href="index.htm" class="cancel">
   ${ToolManager.getText("cancel")}
</a>

</form>

<%@ include file="/templates/footer.jsp" %>
