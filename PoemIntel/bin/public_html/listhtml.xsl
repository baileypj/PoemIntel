<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" indent="yes"/>

<xsl:template match="/">
    <html>
      <head>
      </head>
      <body BGCOLOR="#FFFFFF">
        <xsl:apply-templates select="poems"/>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="poems">
	<xsl:apply-templates select="poem"/>
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

      <!-- Set a variable to hold the href link and title-->
      <xsl:variable name="href"></xsl:variable>
      <xsl:variable name="poem_title"><xsl:value-of select="." /></xsl:variable>

      <a href="{$href}/{$poem_title}.poem">Link for <xsl:value-of select="." /></a>
      <br/>
  </xsl:template>

  <xsl:template match="author">
       Written by <xsl:value-of select="." />
  </xsl:template>

  <xsl:template match="year">
       (<xsl:value-of select="." />)
  </xsl:template>

</xsl:stylesheet>
