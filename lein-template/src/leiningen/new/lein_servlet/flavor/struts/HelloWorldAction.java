package {{sanitized}};

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForward;

import clojure.lang.Var;
import {{sanitized}}.util.Clojure;

public class HelloWorldAction extends org.apache.struts.action.Action {
    
    /* forward name="success" path="" */
    private final static String SUCCESS = "success";
    
    /**
     * This is the action called from the Struts framework.
     * @param mapping The ActionMapping used to select this instance.
     * @param form The optional ActionForm bean for this request.
     * @param request The HTTP Request we are processing.
     * @param response The HTTP Response we are processing.
     * @throws java.lang.Exception
     * @return
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        HelloWorldActionForm helloWorldForm = (HelloWorldActionForm) form;
        helloWorldForm.setMessage("Hello World!");

        // set `extra` based on Clojure call
        Var f = Clojure.getvar("clojure.core", "vector");
        Object v = f.invoke(1, 2, 3, 4, 5);
        helloWorldForm.setExtra(v.toString());

        // sum computed by Clojure code
        Var g = Clojure.getvar("{{name}}.core", "sum");
        Object w = g.invoke(1, 2, 3, 4, 5);
        helloWorldForm.setSum(w.toString());

        return mapping.findForward(SUCCESS);
    }
}
