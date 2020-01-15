<%@ include file="/templates/includes.jsp"%>
<%@ include file="/templates/header.jsp"%>
<%
    request.setCharacterEncoding("UTF-8");
%>

<c:if test="${error != null}" >
    <div class="validation"> ${ToolManager.getText(error)}<c:if test="${etext != null}"> - ${etext}</c:if></div><br/><br/> 
</c:if>

<h3>
    ${ToolManager.getText("editPlaybackHeader")}
</h3>

<form name="formAction" method="post" class="playback_edit" action="playback_edit.htm">
    <p>
        ${ToolManager.getText("editPlayback")} : 
        <input type="text" name="newTitle" value="${title}"/>
        <br/>
        <br/>         
        <input type="submit" name="save" value="${ToolManager.getText("save")}" />
		
		<a href="index.htm" class="button btn-primary">
		   ${ToolManager.getText("cancel")}
		</a>
    </p>
</form>


<%@ include file="/templates/footer.jsp"%>