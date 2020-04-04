<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/1999/xhtml">
    <xsl:output method="html" omit-xml-declaration="yes" indent="no" doctype-public="java.ops"/>
    <xsl:strip-space elements="*"/>
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
                <xsl:apply-templates/>
            </body>
        </html>
    </xsl:template>

    <!--Headers, Table-->
    <xsl:template match="//*[text() = $projectName]">
        <h1>Группы проекта <xsl:value-of select="parent::*/*[name()='courseName']/text()"/></h1>
        <p><strong>Описание: </strong><xsl:copy-of select="parent::*/*[name()='courseDescription']/text()"/></p>
        <table>
            <tr><th>Group Name</th><th>Group Flag</th><th>Number of Users</th></tr>
            <xsl:for-each select="parent::*/*[name()='Groups']/*[name()='Group']">
                <tr>
                    <td><xsl:copy-of select="./*[name()='groupName']/text()"/></td>
                    <td><xsl:copy-of select="./*[name()='groupFlag']/text()"/></td>
                    <td><xsl:value-of select="count(./*[name()='Users']/*[name()='User'])"/></td>
                </tr>
            </xsl:for-each>
        </table>
    </xsl:template>

    <xsl:template match="text()"/>

</xsl:stylesheet>