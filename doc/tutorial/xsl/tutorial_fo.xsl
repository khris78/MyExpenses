<?xml version='1.0'?> 
<xsl:stylesheet  
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                    xmlns:fo="http://www.w3.org/1999/XSL/Format"> 
<xsl:import href="/usr/share/xml/docbook/stylesheet/docbook-xsl/fo/docbook.xsl"/>
<xsl:import href="tutorial_strings.xsl"/>

<xsl:attribute-set name="section.title.properties">
  <xsl:attribute name="clear">both</xsl:attribute>
</xsl:attribute-set>

<xsl:param name="paper.type">A4</xsl:param>
<xsl:param name="generate.toc"></xsl:param>

<xsl:template name="inline.charseq">
  <xsl:param name="content">
  <xsl:variable name="id" select="@role"/>
    <xsl:choose>
  <xsl:when test="normalize-space(.)">
    <xsl:call-template name="simple.xlink">
      <xsl:with-param name="content">
        <xsl:apply-templates/>
      </xsl:with-param>
    </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="getString">
      <xsl:with-param name="id" select="$id"/>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
  </xsl:param>

  <xsl:choose>
    <xsl:when test="@dir">
      <fo:inline>
        <xsl:attribute name="direction">
          <xsl:choose>
            <xsl:when test="@dir = 'ltr' or @dir = 'lro'">ltr</xsl:when>
            <xsl:otherwise>rtl</xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
        <xsl:copy-of select="$content"/>
      </fo:inline>
    </xsl:when>
    <xsl:otherwise>
      <xsl:copy-of select="$content"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- overriden for setting end.indent -->
<xsl:template match="figure">
  <xsl:variable name="param.placement"
              select="substring-after(normalize-space($formal.title.placement),
                                      concat(local-name(.), ' '))"/>

  <xsl:variable name="placement">
    <xsl:choose>
      <xsl:when test="contains($param.placement, ' ')">
        <xsl:value-of select="substring-before($param.placement, ' ')"/>
      </xsl:when>
      <xsl:when test="$param.placement = ''">before</xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$param.placement"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="figure">
    <xsl:choose>
      <xsl:when test="@pgwide = '1'">
        <fo:block xsl:use-attribute-sets="pgwide.properties">
          <xsl:call-template name="formal.object">
            <xsl:with-param name="placement" select="$placement"/>
          </xsl:call-template>
        </fo:block>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="formal.object">
          <xsl:with-param name="placement" select="$placement"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="floatstyle">
    <xsl:call-template name="floatstyle"/>
  </xsl:variable>

  <xsl:choose>
    <xsl:when test="$floatstyle != ''">
      <xsl:call-template name="floater">
        <xsl:with-param name="position" select="$floatstyle"/>
        <xsl:with-param name="content" select="$figure"/>
        <xsl:with-param name="end.indent" select="'1em'"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:copy-of select="$figure"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="processing-instruction('float-clear')">
   <fo:block clear="both"/>
</xsl:template>
<!-- suppress caption in figure -->
<xsl:template name="formal.object.heading"/>


</xsl:stylesheet>
