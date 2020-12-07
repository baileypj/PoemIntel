package poem;

/**
 * Classing representing a Poem object.
 */
public class Poem
{

  private PubInfo pubInfo;
  private String body;

  /**
   * Constructor.
   */
  public Poem(PubInfo pubInfo, String body)
  {
    this.pubInfo = pubInfo;
    this.body = body;
  }

  /**
   * Retrieve the pubInfo object.
   *
   * @return poem publication info object
   */
  public PubInfo getPubInfo()
  {
    return pubInfo;
  }

  /**
   * Retrieve the body of a poem.
   *
   * @return body of poem
   */
  public String getBody()
  {
    return body;
  }
  
  /**
   * Retrieve the poem as an XML String
   * 
   * @return poem XML as a String
   */
  public String getPoemAsXML()
  {
    return "<poem\r\n" +
        "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" +
        "xsi:noNamespaceSchemaLocation=\"poem.xsd\">\r\n" +
        "<pubInfo>\r\n" +
        "    <title>" + pubInfo.getTitle() + "</title>\r\n" +
        "    <author>" + pubInfo.getAuthor() + "</author>\r\n" +
        "    <year>" + pubInfo.getYear() + "</year>\r\n" +
        "  </pubInfo>\r\n" +
        "  <body>" + getBody() +"</body>\r\n" +
        "</poem>\r\n";
  }

}
