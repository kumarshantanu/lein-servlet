package {{sanitized}}.util;

import java.util.concurrent.ConcurrentHashMap;

import clojure.lang.Var;
import clojure.lang.RT;
import clojure.lang.Symbol;

public class Clojure {

  private static final ConcurrentHashMap<String, Var> VARS =
    new ConcurrentHashMap<String, Var>();

  private static Var getFromCache(String ns, String name) {
    String key = "" + ns + "/" + name;
    return VARS.get(key);
  }

  private static void putIntoCache(String ns, String name, Var v) {
    String key = "" + ns + "/" + name;
    VARS.put(key, v);
  }

  public static Var getvar(String nameSpace, String name) {
    Var v = getFromCache(nameSpace, name);
    if (v != null) {
      return v;
    }
    RT.var("clojure.core", "require").invoke(Symbol.intern(nameSpace));
    v = RT.var(nameSpace, name);
    putIntoCache(nameSpace, name, v);
    return v;
  }

}
