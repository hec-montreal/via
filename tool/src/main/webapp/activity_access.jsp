<%@ include file="/templates/includes.jsp" %>
<%@ include file="/templates/header.jsp" %>

<c:if test="${error != null}" >
    <div class="validation"> ${ToolManager.getText(error)}<c:if test="${etext != null}"> - ${etext}</c:if></div><br/><br/> 
</c:if>

<%@ include file="/templates/footer.jsp" %>