<?xml version="1.0" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" indent="yes"/>

<xsl:template match="/">
    <html>
      <head>
      </head>
      <body BGCOLOR="#FFFFFF">
        <xsl:apply-templates select="poem"/>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="poem">
    <h2>
      <xsl:apply-templates select="pubInfo"/>
    </h2>


  </xsl:template>

  <xsl:template match="pubInfo">
      <xsl:apply-templates select="title"/>
      <xsl:apply-templates select="author"/>
      <xsl:apply-templates select="year"/>
      <!-- Poem Link can go here if it can get access to poem name, maybe through title/text()? -->

  </xsl:template>

  <xsl:template match="title">
       "<xsl:value-of select="." />"
       <!-- Not sure which of these will work -->
       Poem Link: <a href="127.0.0.1:8080/"><xsl:value-of select=".".poem/></a>
       Poem Link: <a href="127.0.0.1:8080/<xsl:value-of select=".".poem/>"></a>
  </xsl:template>

  <xsl:template match="author">
       Written by <xsl:value-of select="." />
  </xsl:template>

  <xsl:template match="year">
       (<xsl:value-of select="." />)
  </xsl:template>

</xsl:stylesheet>
