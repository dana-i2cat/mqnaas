/*
 * Extracted from org.opendaylight.controller.sal.action in OpenDaylight Hellium release
 */

/*
 * Copyright (c) 2013-2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
// package org.opendaylight.controller.sal.action;

/*
 * #%L
 * MQNaaS :: ODL Model
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i
 * 			Innovació a Catalunya
 * %%
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
 * #L%
 */
package org.mqnaas.extensions.odl.hellium.flowprogrammer.model;

/**
 * The enumeration of actions supported by the controller
 * Each entry has a unique id and the values range for the action element where applicable
 */
public enum ActionType {
    DROP("drop", 0, 0),
    LOOPBACK("loopback", 0, 0),
    FLOOD("flood", 0, 0), // regular switching flood (obeys to stp port state)
    FLOOD_ALL("floodAll", 0, 0), // flood to all ports regardless of stp port state
    CONTROLLER("controller", 0, 0),
    INTERFACE("interface", 0, 0), // Interface
    SW_PATH("software path", 0, 0), // OF Local
    HW_PATH("harware path", 0, 0),
    OUTPUT("output", 0, 0xffff), // physical port
    ENQUEUE("enqueue", 0, 0xffff),
    SET_DL_SRC("setDlSrc", 0, 0),
    SET_DL_DST("setDlDst", 0, 0),
    SET_VLAN_ID("setVlan", 1, 0xfff),
    SET_VLAN_PCP("setVlanPcp", 0, 0x7),
    SET_VLAN_CFI("setVlanCif", 0, 0x1),
    POP_VLAN("stripVlan", 0, 0), // Pop
    PUSH_VLAN("pushVlan", 0, 0xffff), // Push (the max value only takes into account the TCI portion of the 802.1q header)
    SET_DL_TYPE("setDlType", 0, 0xffff), // Set ethertype/length field
    SET_NW_SRC("setNwSrc", 0, 0),
    SET_NW_DST("setNwDst", 0, 0),
    SET_NW_TOS("setNwTos", 0, 0x3f),
    SET_TP_SRC("setTpSrc", 0, 0xffff), // Set transport source port
    SET_TP_DST("setTpDst", 0, 0xffff), // Set transport destination port
    SET_NEXT_HOP("setNextHop", 0, 0);

    private String id;
    private int minValue;
    private int maxValue;

    private ActionType(String id, int minValue, int maxValue) {
        this.id = id;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public String getId() {
        return id;
    }

    public boolean isValidTarget(int value) {
        return (value >= minValue && value <= maxValue);
    }

    public String getRange() {
        return "[0x" + Long.toHexString(minValue) + "-0x" + Long.toHexString(maxValue) + "]";
    }

    public boolean takesParameter() {
        switch (this) {
        case POP_VLAN:
        case DROP:
        case SW_PATH:
        case HW_PATH:
        case CONTROLLER:
        case LOOPBACK:
        case FLOOD:
        case FLOOD_ALL:
            return false;
        default:
            return true;
        }
    }

    public int calculateConsistentHashCode() {
        if (this.id != null) {
            return this.id.hashCode();
        } else {
            return 0;
        }
    }
}
