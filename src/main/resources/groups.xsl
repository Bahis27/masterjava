<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/1999/xhtml" xmlns:p="http://javaops.ru">

    <xsl:output method="html" omit-xml-declaration="yes" indent="yes" doctype-public="java.ops"/>
    <xsl:param name="projectName"/>

    <!--XHTML document-->
    <xsl:template match="/">
        <html lang="ru">
        <head>
            <title>Пользователи</title>
            <style type="text/css">
                h1          { padding: 10px; padding-width: 100%; background-color: silver }
                td, th      { width: 40%; border: 1px solid silver; padding: 10px }
                td:first-child, th:first-child  { width: 20% }
                table       { width: 650px }
            </style>
        </head>
            <body>
                <h1>Группы проекта <xsl:value-of select="$projectName"/></h1>
                <p><strong>Описание: </strong><xsl:value-of select="/p:Payload/p:Projects/p:Project/p:courseName[text()=$projectName]/../p:courseDescription/text()"/></p>
                <table>
                    <tr><th>Group Name</th><th>Group Flag</th><th>Number of Users</th></tr>
                    <xsl:for-each select="/p:Payload/p:Projects/p:Project/p:courseName[text()=$projectName]/../p:Groups/p:Group">
                        <tr>
                            <td><xsl:value-of select="./p:groupName/text()"/></td>
                            <td><xsl:value-of select="./p:groupFlag/text()"/></td>
                            <td><xsl:value-of select="count(./p:Users/p:User)"/></td>
                        </tr>
                    </xsl:for-each>
                </table>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>