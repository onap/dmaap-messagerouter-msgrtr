/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package  org.onap.dmaap.mr.filter;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.servlet.ServletException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.onap.dmaap.mr.filter.ContentLengthFilter;

public class ContentLengthFilterTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDestroy() {
		ContentLengthFilter filter = new ContentLengthFilter();
		
		filter.destroy();
		
		assertTrue(true);
	}
	
	
	@Test
	public void testFilter() {
		ContentLengthFilter filter = new ContentLengthFilter();
		
		try {
			filter.doFilter(null, null, null);
		} catch (IOException | ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			assertTrue(true);
		}
		
		
	}
	
	@Test
	public void testInit() {
		ContentLengthFilter filter = new ContentLengthFilter();
		
		try {
			filter.init(null);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			assertTrue(true);
		}		
		
	}


}