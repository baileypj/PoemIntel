<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" indent="yes"/>

<xsl:template match="/">
    <xsl:apply-templates select="poems"/>
  </xsl:template>

  <xsl:template match="poems">
	<xsl:apply-templates select="poem"/>
  </xsl:template>

  <xsl:template match="poem">
    <xsl:apply-templates select="pubInfo"/>
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

</xsl:stylesheet>
