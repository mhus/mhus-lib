/**
 * Copyright 2018 Mike Hummel
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.lib.portlet.resource;

import java.io.IOException;
import java.io.StringWriter;

import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;

/**
 * Abstract AjaxResource class.
 *
 * @author mikehummel
 * @version $Id: $Id
 */
public abstract class AjaxResource extends AbstractResource {

    /** {@inheritDoc} */
    @Override
    public boolean serveResource(
            String path, ResourceRequest resourceRequest, ResourceResponse resourceResponse)
            throws IOException, PortletException {

        JsonFactory f = new JsonFactory();

        if (isDirectWriteEnabled()) {
            resourceResponse.setContentType("application/json");
            resourceResponse.resetBuffer();
            resourceResponse.setCharacterEncoding("UTF-8");
            JsonGenerator out = f.createJsonGenerator(resourceResponse.getWriter());

            doRequest(resourceRequest, out);
            out.close();

            resourceResponse.flushBuffer();
        } else {
            StringWriter sw = new StringWriter();
            JsonGenerator out = f.createJsonGenerator(sw);

            doRequest(resourceRequest, out);
            out.close();

            resourceResponse.setContentType("application/json");
            resourceResponse.resetBuffer();
            resourceResponse.setCharacterEncoding("UTF-8");
            resourceResponse.getWriter().print(sw.toString());
            resourceResponse.flushBuffer();
        }
        return true;
    }

    /**
     * isDirectWriteEnabled.
     *
     * @return a boolean.
     */
    protected boolean isDirectWriteEnabled() {
        return false;
    }

    /**
     * doRequest.
     *
     * @param request a {@link javax.portlet.ResourceRequest} object.
     * @param out a {@link org.codehaus.jackson.JsonGenerator} object.
     * @throws java.io.IOException if any.
     * @throws javax.portlet.PortletException if any.
     */
    protected abstract void doRequest(ResourceRequest request, JsonGenerator out)
            throws IOException, PortletException;
}