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

}
