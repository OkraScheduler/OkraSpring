/*
 *  Copyright (c) 2016 Fernando Nogueira
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package okra.model;

import lombok.Data;
import okra.base.OkraItem;
import okra.base.OkraStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;

import java.time.LocalDateTime;

@CompoundIndexes(
        {
                @CompoundIndex(def = "{'runDate':1,'status':1}", name = "i_runDate_status"),
                @CompoundIndex(def = "{'status':1,'heartbeat':1}", name = "i_status_heartbeat"),
                @CompoundIndex(def = "{'_id':-1,'status':1,'heartbeat':1}", name = "i_id_heartbeat_status")
        }
)
@Data
public class DefaultOkraItem implements OkraItem {

    @Id
    private String id;

    private LocalDateTime heartbeat;

    private LocalDateTime runDate;

    private OkraStatus status;

}
