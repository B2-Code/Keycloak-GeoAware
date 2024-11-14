<#import "template.ftl" as layout>
<@layout.emailLayout>
    ${kcSanitize(msg("loginFromNewDeviceBodyHtml", user.username, date, ip, city, country, browser, os))?no_esc}
</@layout.emailLayout>