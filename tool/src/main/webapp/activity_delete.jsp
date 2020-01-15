<%@ include file="/templates/includes.jsp" %>
<%@ include file="/templates/header.jsp" %>

<c:if test="${error != null}" >
    <div class="validation"> ${ToolManager.getText(error)}<c:if test="${etext != null}"> - ${etext}</c:if></div><br/><br/> 
</c:if>

<h3>${ToolManager.getText("deleteHeader")}</h3>

<div class="validation">${ToolManager.getText("deleteText")}</div>
<br/>
<br/>

<a href="activity_delete.htm?action=1"><button type="button">${ToolManager.getText("delete")}</button></a>
<a href="index.htm"><button type="button">${ToolManager.getText("cancel")}</button></a>

    
<%@ include file="/templates/footer.jsp" %>