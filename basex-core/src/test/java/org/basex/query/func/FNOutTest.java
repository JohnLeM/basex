package org.basex.query.func;

import static org.basex.query.func.Function.*;

import org.basex.query.*;
import org.junit.*;

/**
 * This class tests the functions of the Output Module.
 *
 * @author BaseX Team 2005-13, BSD License
 * @author Christian Gruen
 */
public final class FNOutTest extends AdvancedQueryTest {
  /** Test method. */
  @Test
  public void nl() {
    // query() function removes all newlines..
    query(_OUT_NL.args(), "");
  }

  /** Test method. */
  @Test
  public void tab() {
    query(_OUT_TAB.args(), "\t");
  }

  /** Test method. */
  @Test
  public void format() {
    query(_OUT_FORMAT.args("x", "x"), "x");
    query(_OUT_FORMAT.args("%d", " 1"), "1");
    query(_OUT_FORMAT.args("%2d", " 1"), " 1");
    query(_OUT_FORMAT.args("%05d", " 123"), "00123");
  }
}
