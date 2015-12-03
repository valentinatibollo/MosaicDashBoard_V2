To integrate UC1 and UC2&3 it is necessary to add a login page.
index.html shows a splash screen where user can choose between UC1 and UC2&3.
to access UC1, in index.html change line 50 with the correct address
<div style="cursor:pointer;" onclick="location.href='uc1_pages/login.html'"><img src="images/uc1.png" height="96" width="350"></div>

in UC2&3 to set properly Home redirect and Log Out, in dashboard.html change at line 25 and 28
onclick="history.go(-1);"
with correct address.