package cc.creamcookie.views;

import cc.creamcookie.CCSApplication;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import lombok.RequiredArgsConstructor;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.sql.Date;
import java.text.DecimalFormat;
import java.util.*;

@Component
@RequiredArgsConstructor
public class ViewExtenstion extends AbstractExtension {

    private final MessageSourceAccessor messageSourceAccessor;

    public Map<String, Filter> getFilters() {

        Map<String, Filter> filters = new HashMap<>(2);
        filters.put("ts", new FilterTimestamp());
        filters.put("nf", new FilterNumbFormat());
        filters.put("toJSON", new FilterJson());
        filters.put("crlf", new FilterCrLf());
        return filters;
    }

    @Override
    public Map<String, Function> getFunctions() {

        Map<String, Function> functions = new HashMap<>(2);
        functions.put("url", new FunctionUrl());
        functions.put("lang", new FunctionLang(messageSourceAccessor));
        functions.put("pRange", new FunctionPRange());
        return functions;
    }

    private static class FunctionUrl implements Function {
        @Override
        public List<String> getArgumentNames() {
            return Arrays.asList(new String[]{"path", "param", "nocache", "addparam"});
        }

        @SuppressWarnings("unchecked")
        @Override
        public Object execute(Map<String, Object> arg0, PebbleTemplate self, EvaluationContext context, int lineNumber) {
            String url = "";
            HttpServletRequest request = CCSApplication.getCurrentRequest();
            if (request != null) {

                String path = arg0.get("path").toString().trim();
                if (!path.startsWith("/")) {
                    String suffix = request.getRequestURL().toString();
                    suffix = suffix.substring(0, suffix.lastIndexOf("/") + 1);
                    URI u = URI.create(suffix + path).normalize();
                    url = u.toString();
                } else {
                    url = request.getContextPath() + path;
                }

                if (arg0.containsKey("param") && arg0.get("param") != null && request.getQueryString() != null) {
                    try {
                        List<String> arr = (List<String>) arg0.get("param");

                        StringBuilder params = new StringBuilder();
                        String[] queries = request.getQueryString().split("&");
                        for (String query : queries) {
                            if (query.length() > 0 && arr != null && arr.size() > 0 && arr.contains(query.substring(0, query.indexOf("="))))
                                continue;
                            params.append("&" + query);
                        }

                        if (params.toString() != null && !params.toString().trim().isEmpty()) {
                            String result = params.toString().substring(1);
                            url += (url.contains("?") ? "&" : "?") + result;
                        }
                    } catch (Exception ex) {
                    }
                }
            } else {
                url = arg0.get("path").toString();
            }

            if ((Boolean) arg0.getOrDefault("nocache", false)) {
                url += (url.contains("?") ? "&" : "?") + CCSApplication.START_TIME;
            }

            if (!arg0.getOrDefault("addparam", "").toString().isEmpty()) {
                url += (url.contains("?") ? "&" : "?") + arg0.getOrDefault("addparam", "");
            }

            return url;
        }
    }

    private static class FilterCrLf implements Filter {
        @Override
        public List<String> getArgumentNames() {
            return null;
        }

        @Override
        public Object apply(Object input, Map<String, Object> arg1, PebbleTemplate self, EvaluationContext context, int lineNumber) throws PebbleException {
            if (input == null) return null;
            try {
                return input.toString().replaceAll("\n", "<br />");
            } catch (Exception ex) {
                return input;
            }
        }


    }

    private static class FilterJson implements Filter {
        @Override
        public List<String> getArgumentNames() {
            return null;
        }

        @Override
        public Object apply(Object input, Map<String, Object> arg1, PebbleTemplate self, EvaluationContext context,
                            int lineNumber) throws PebbleException {
            if (input == null) return null;
            try {
                return JSONArray.fromObject(input);
            } catch (Exception ex) {
                return JSONObject.fromObject(input);
            }
        }
    }

    private static class FilterNumbFormat implements Filter {

        @Override
        public List<String> getArgumentNames() {
            return Arrays.asList(new String[]{ "size", "hour" });
        }

        @Override
        public Object apply(Object input, Map<String, Object> arg1, PebbleTemplate self, EvaluationContext context,
                            int lineNumber) throws PebbleException {
            if (input == null) return null;
            Integer i = Integer.parseInt(input.toString());

            if (arg1.get("hour") != null && arg1.get("hour").equals(true)) i = i % 24;

            DecimalFormat df = new DecimalFormat("00");
            return df.format(i);
        }
    }

    private static class FilterTimestamp implements Filter {
        @Override
        public List<String> getArgumentNames() {
            return null;
        }

        @Override
        public Object apply(Object input, Map<String, Object> arg1, PebbleTemplate self, EvaluationContext context,
                            int lineNumber) throws PebbleException {
            if (input == null) return null;
            return new Date(Long.parseLong(input.toString()) * 1000);
        }
    }

    private static class FunctionLang implements Function {

        private MessageSourceAccessor messageSource;

        public FunctionLang(MessageSourceAccessor messageSource) {
            this.messageSource = messageSource;

        }

        @Override
        public List<String> getArgumentNames() {
            return Arrays.asList(new String[]{"arg0", "arg1", "arg2", "ignore"});
        }

        @Override
        public Object execute(Map<String, Object> arg0, PebbleTemplate self, EvaluationContext context, int lineNumber) {

            StringBuilder sb = new StringBuilder();
            sb.append(arg0.get("arg0"));
            if (arg0.get("arg1") != null) {
                sb.append((!sb.toString().isEmpty()) ? "." : "");
                sb.append(arg0.get("arg1"));
            }
            if (arg0.get("arg2") != null) {
                sb.append((!sb.toString().isEmpty()) ? "." : "");
                sb.append(arg0.get("arg2"));
            }

            try {
                return messageSource.getMessage(sb.toString());
            } catch (Exception ex) {
                if (arg0.get("ignore") != null && Boolean.parseBoolean(arg0.get("ignore").toString()) == true)
                    return "";
                return arg0.get("arg1") != null ? arg0.get("arg1") : arg0.get("arg0");
            }

        }
    }

    private static class FunctionPRange implements Function {

        @Override
        public List<String> getArgumentNames() {
            return Arrays.asList(new String[]{"arg0"});
        }

        @SuppressWarnings("rawtypes")
        @Override
        public Object execute(Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber) {

            List<Integer> range = new ArrayList<Integer>();

            Page rows = (Page) args.get("arg0");
            if (rows == null) return range;

            int page = rows.getNumber() + 1;
            int pageSize = rows.getTotalPages();

            int min;
            int max;

            if (pageSize < 6) {
                min = 1;
                max = pageSize;
            } else if (page - 2 > 0) {
                if (page + 2 < pageSize) {
                    min = page - 2;
                    max = page + 2;
                } else {
                    min = pageSize - 4;
                    max = pageSize;
                }
            } else {// 필요없을 것 같은데...
                int value = 3 - page;

                if (page + value > pageSize) {
                    min = 1;
                    max = pageSize;
                } else {
                    min = 1;
                    max = page + 2 + value;
                }
            }

            for (int i = min; i <= max; i++) {
                range.add(i);
            }

            return range;
        }

    }

}
