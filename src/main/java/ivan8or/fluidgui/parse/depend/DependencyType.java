package ivan8or.fluidgui.parse.depend;

import java.util.List;
import java.util.Map;

public enum DependencyType {

    TRANSITION() {
          public List<Map<String, Object>> cast(Object a) {
              return (List<Map<String, Object>>) a;
          }
      },
      ITEM() {
          public Map<String, Object> cast(Object a) {
              return (Map<String, Object>) a;
          }
      };

}
