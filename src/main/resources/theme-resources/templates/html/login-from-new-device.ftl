<#import "template.ftl" as layout>
<@layout.emailLayout>
    ${kcSanitize(msg("loginFromNewDeviceBodyHtml", user.username, date, ip, city, country, browser, os, resetCredentialsHelp))?no_esc}
</@layout.emailLayout>