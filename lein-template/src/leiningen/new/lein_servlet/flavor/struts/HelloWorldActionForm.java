package {{sanitized}};

public class HelloWorldActionForm extends org.apache.struts.action.ActionForm {

    private String message;
    private String extra;
    private String sum;

    /**
     *
     */
    public HelloWorldActionForm() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    public String getExtra() {
      return extra;
    }

    public void setExtra(String extra) {
      this.extra = extra;
    }

    public String getSum() {
      return sum;
    }

    public void setSum(String sum) {
      this.sum = sum;
    }

}
