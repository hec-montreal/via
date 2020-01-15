<%@ include file="/templates/includes.jsp"%>
<%@ include file="/templates/header.jsp"%>

<c:if test="${error != null}">
    <div class="validation">${error}</div><br/><br/>
</c:if>

<h3>
    ${ToolManager.getText("permissions")}
</h3>

<form action="" method="POST" class="permissions">
    <table class="listHier checkGrid" cellspacing="0" border="0" style="width:auto">
        <tr class="evenrow">
            <th class=="permission">
                 ${ToolManager.getText("permissions")}
            </th>
            <c:forEach items="${listPermissions}" var="permission">
                <th class="role">
                    <c:out value="${permission.userType}"/>
                </th>
            </c:forEach>
        </tr>
         
        <tr>
            <td class="permissionDescription" >
                ${ToolManager.getText("canCreateActivity")}
            </td>
            <c:forEach items="${listPermissions}" var="permission">
                <td class="${permission.canCreateActivity == true ? 'checkboxCell active' : 'checkboxCell'}">
                    <input type="checkbox" class="checkbox" name="c_${permission.userType}" id="c_${permission.userType}" <c:if test="${permission.canCreateActivity == true}"> checked </c:if> <c:if test="${permission.userType == adminType}"> checked disabled </c:if>/>
                </td>
            </c:forEach>
        </tr>
         
        <tr class="evenrow">
            <td class="permissionDescription">
                ${ToolManager.getText("canEditActivity")}
            </td>
            <c:forEach items="${listPermissions}" var="permission">
                <td class="${permission.canEditActivity == true ? 'checkboxCell active' : 'checkboxCell'}">
                    <input type="checkbox" class="checkbox" name="e_${permission.userType}" id="e_${permission.userType}" <c:if test="${permission.canEditActivity == true}"> checked </c:if> <c:if test="${permission.userType == adminType}"> checked disabled </c:if> />
                </td>
            </c:forEach>
        </tr>
         
        <tr>
            <td class="permissionDescription">
                ${ToolManager.getText("canSeeAllActivities")}
            </td>
            <c:forEach items="${listPermissions}" var="permission">
                <td class="${permission.canSeeAllActivities == true ? 'checkboxCell active' : 'checkboxCell'}">
                    <input type="checkbox" class="checkbox" name="s_${permission.userType}" id="s_${permission.userType}" <c:if test="${permission.canSeeAllActivities == true}"> checked </c:if> <c:if test="${permission.userType == adminType}"> checked disabled </c:if> />
                </td>
            </c:forEach>
        </tr>
         
        <tr>
            <td class="permissionDescription">
                ${ToolManager.getText("canEditPermissions")}
            </td>
            <c:forEach items="${listPermissions}" var="permission">
                <td class="${permission.canEditPermissions == true ? 'checkboxCell active' : 'checkboxCell'}">
                    <input type="checkbox" class="checkbox" name="p_${permission.userType}" id="p_${permission.userType}" <c:if test="${permission.canEditPermissions == true}"> checked </c:if> <c:if test="${permission.userType == adminType}"> checked disabled </c:if> />
                </td>
            </c:forEach>
        </tr>
    </table>
    <br/>
    <br/>
    
    <input type="submit" name="submit" value="${ToolManager.getText("save")}"/>
    <input type="submit" name="cancel" value="${ToolManager.getText("cancel")}"/>
    
</form>

<script>

$("input[type=checkbox]").change(function() {
    $(this).parent().toggleClass("active");
});

</script>
<%@ include file="/templates/footer.jsp"%>