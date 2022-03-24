/**
 * <a href="http://www.openolat.org">
 * OpenOLAT - Online Learning and Training</a><br>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); <br>
 * you may not use this file except in compliance with the License.<br>
 * You may obtain a copy of the License at the
 * <a href="http://www.apache.org/licenses/LICENSE-2.0">Apache homepage</a>
 * <p>
 * Unless required by applicable law or agreed to in writing,<br>
 * software distributed under the License is distributed on an "AS IS" BASIS, <br>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
 * See the License for the specific language governing permissions and <br>
 * limitations under the License.
 * <p>
 * Initial code contributed and copyrighted by<br>
 * frentix GmbH, http://www.frentix.com
 * <p>
 */
package uk.ac.ed.ph.jqtiplus.node.item.interaction;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * 
 * Initial date: 07.06.2021<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */

@RunWith(Parameterized.class)
public class ExtendedTextInteractionCountTest {
	
	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "Schöner als Umlaut", 3 },
                { "Frühlingsgemüsebrühwürfel mag ich nicht.\nWirklich, nicht, nicht.", 7 },
                { "\"Hello\",world;éco  , été.", 4},
                { "été\n\rséparé.", 2 },
                { "l'avion.", 1 },
                { "<p>lorem ipsum check</p>\n<p>that et kanu unchou</p>\n<p><span class=\"math\" title=\"x%5E2\">x^2</span></p>\n<p>jump&nbsp;</p>\n<p>&nbsp;</p>", 9},
                { "<p>lorem ipsum check</p>\n<p>that et kanu unchou</p>\n<p><span class=\"math\" title=\"x%5E2\">x^2 <span><br>dont count me must</span></span></p>\n<p>jump&nbsp;</p>\n<p>&nbsp;</p>", 9}
        });
    }
    
    private String text;
    private Integer count;
    
    public ExtendedTextInteractionCountTest(String text, Integer count) {
    	this.text = text;
    	this.count = count;
    }
    
    
	@Test
	public void count() {
		int numOfWords = ExtendedTextInteraction.countWords(text);
		Assert.assertEquals(count.intValue(), numOfWords);
	}
}
