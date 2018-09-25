/**
 *  NetLinx Relay Switch
 *
 *  Copyright 2018 Ryan Fletcher
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
metadata {
	definition (name: "NetLinx Relay Switch", namespace: "RAFletcher", author: "Ryan Fletcher") {
		capability "Switch"
        capability "Button"
	}


	simulator {
		// TODO: define status and reply messages here
	}

	tiles(scale: 2) {
		standardTile("switch", "device.switch",width: 6, height: 4, canChangeIcon: true, decoration: "flat") {
        	state "off", label: '${currentValue}', action: "switch.on",
            	icon: "st.switches.switch.off", backgroundColor: "#ffffff"
            state "on", label: '${currentValue}', action: "switch.off",
            	icon: "st.switches.switch.on", backgroundColor: "#00a0dc"
        }
        main("switch")
        
        details(["switch"])
	}
    
    preferences {
        section("Netlinx Info") {
            input "netlinxIp", "text", title: "Netlinx Master IP Address", required: true
            input "netlinxPort", "text", title: "Netlinx Master Port Number", required: true
            }
        }
    }

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
	// TODO: handle 'switch' attribute
	def message = parseLanMessage(description)
    log.debug "Message '${message}'"
    
    def json = parseJson(message.body)
    log.debug "JSON Body '${json.name},${json.value}'"
    
    //sendEvent(name: "switch", value: "${json.value}")
    if(json.name.equals("Relay")) {
    	sendEvent(name: "switch",value: "${json.value}")
    }
    else if(json.name.equals("Outlet")) {
    	switch(json.value)
		{
        	case "on":
            	log.debug "Setting outlet to on"
                createEvent(name: "button", value: "pushed", data: [buttonNumber: 1], descriptionText: "", isStateChange: true)
                break
            
            case "off":
            	log.debug "Setting outlet to off"
                createEvent(name: "button", value: "pushed", data: [buttonNumber: 2], descriptionText: "", isStateChange: true)
                break
            
        }
    }
    
}

// handle commands
def on() {
	log.debug "Executing 'on'"
	// TODO: handle 'on' command
    sendStringToNetlinx("on")
    //sendEvent(label: "switch", value: "on")
}

def off() {
	log.debug "Executing 'off'"
	// TODO: handle 'off' command
    sendStringToNetlinx("off")
    //sendEvent(label: "switch", value: "off")
}

def sendStringToNetlinx(String str) {
	def result = new physicalgraph.device.HubAction(
    	method: "GET",
        path: "/",
        headers: [
        	HOST: "${netlinxIp}:${netlinxPort}"
        ],
        body: "${device.label},${str}"
    )
    
    sendHubCommand(result)
    log.debug "Executing ${str}"
    log.debug result
}