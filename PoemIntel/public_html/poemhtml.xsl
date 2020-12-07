<?xml version="1.0" encoding="UTF-8"?>
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
    <body BGCOLOR="#D3D3D3">
      <xsl:apply-templates select="body"/>
    </body>
  </xsl:template>

  <xsl:template match="pubInfo">
      <xsl:apply-templates select="title"/>
      <xsl:apply-templates select="author"/>
      <xsl:apply-templates select="year"/>
  </xsl:template>

  <xsl:template match="title">
       "<xsl:value-of select="." />"
  </xsl:template>

  <xsl:template match="author">
       Written by <xsl:value-of select="." />
  </xsl:template>

  <xsl:template match="year">
       (<xsl:value-of select="." />)
  </xsl:template>

  <xsl:template match="body">
       <xsl:value-of select="." />
  </xsl:template>


</xsl:stylesheet>
