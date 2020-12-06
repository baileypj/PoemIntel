/**
 * Class representing the publication information object of a poem.
 */
public class PubInfo
{

  private String title;
  private String author;
  private String year;

  /**
   * Constructor.
   */
  public PubInfo(String title, String author, String year)
  {
    this.title = title;
    this.author = author;
    this.year = year;
  }

  /**
   * Retrieve the title.
   * 
   * @return title of poem
   */
  public String getTitle()
  {
    return title;
  }

  /**
   * Retrieve the author.
   * 
   * @return author of poem
   */
  public String getAuthor()
  {
    return author;
  }

  /**
   * Retrieve the year.
   * 
   * @return year of poem
   */
  public String getYear()
  {
    return year;
  }

}
