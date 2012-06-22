package org.basex.http.restxq;

import static org.basex.util.Token.*;

/**
 * This class assembles texts which are used in the HTTP classes.
 *
 * @author BaseX Team 2005-12, BSD License
 * @author Christian Gruen
 */
interface RestXqText {
  /** Annotation "path". */
  byte[] PATH = token("path");
  /** Annotation "produces". */
  byte[] PRODUCES = token("produces");
  /** Annotation "consumes". */
  byte[] CONSUMES = token("consumes");
  /** Annotation "query-param". */
  byte[] QUERY_PARAM = token("query-param");
  /** Annotation "form-param". */
  byte[] FORM_PARAM = token("form-param");
  /** Annotation "header-param". */
  byte[] HEADER_PARAM = token("header-param");
  /** Annotation "cookie-param". */
  byte[] COOKIE_PARAM = token("cookie-param");
  /** Annotation "session-id". */
  byte[] SESSION_ID = token("session-id");

  /** Token "header". */
  byte[] HEADER = token("header");
  /** Token "response". */
  byte[] RESPONSE = token("response");
  /** Token "status". */
  byte[] STATUS = token("status");
  /** Token "reason". */
  byte[] REASON = token("reason");
  /** Token "name". */
  byte[] NAME = token("name");
  /** Token "value". */
  byte[] VALUE = token("value");

  /** Error message. */
  String ANN_MISSING = "Annotation % is missing.";
  /** Error message. */
  String ANN_TWICE = "Annotation %% is specified more than once.";
  /** Error message. */
  String ANN_UNKNOWN = "Annotation %% is invalid or not supported.";
  /** Error message. */
  String ANN_PARAMS = "Annotation %% requires at least 2 parameters.";
  /** Error message. */
  String ANN_STRING = "Value of annotation %% is no string: %.";
  /** Error message. */
  String INV_TEMPLATE = "Invalid path template: \"%\".";
  /** Error message. */
  String INV_VARNAME = "Invalid variable name: $%.";
  /** Error message. */
  String INV_VARTYPE = "Variable $% must inherit from %.";
  /** Error message. */
  String UNKNOWN_VAR = "Variable $% is not specified as argument.";
  /** Error message. */
  String VAR_ASSIGNED = "Variable $% is specified more than once.";
  /** Error message. */
  String VAR_UNDEFINED = "Variable $% is not assigned by the annotations.";
  /** Error message. */
  String UNKNOWN_SER = "Unknown serialization parameter \"%\".";
  /** Error message. */
  String HEAD_METHOD = "HEAD method must only return 'rest:reponse' element.";
  /** Error message. */
  String METHOD_VALUE = "Method % does not allow values.";
  /** Error message. */
  String SESSION_VALUE = "Annotation % requires a value.";
  /** Error message. */
  String INPUT_CONV = "Input could not be converted: %";
}
