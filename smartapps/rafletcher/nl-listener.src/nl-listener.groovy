/**
 *  NL Listener
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
definition(
    name: "NL Listener",
    namespace: "RAFletcher",
    author: "Ryan Fletcher",
    description: "Netlinx stuff",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Title") {
		input "theSwitch", "capability.switch"
        input "theButton", "capability.button"
	}
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	subscribe(theButton,"button",buttonEvent)
    subscribe(theSwitch,"switch",switchEvent)
}

def buttonEvent(evt) {
	log.debug "Button event received: ${evt.data},${evt.value}"
    def json = parseJson(evt.data)
    def value = evt.value
    
    if(json.buttonNumber.equals(1)) {
    	theSwitch.on()
    }
    else if(json.buttonNumber.equals(2)) {
    	theSwitch.off()
    }
}

def switchEvent(evt) {
	log.debug "switch event received: ${evt.value}"
    
    if(evt.value.equals("on")) {
    	sendStringToNetlinx("Outlet","on")
    }
    else {
    	sendStringToNetlinx("Outlet","off")
    }
}	
def sendStringToNetlinx(String label, String str) {
	def result = new physicalgraph.device.HubAction(
    	method: "GET",
        path: "/",
        headers: [
        	HOST: "192.168.1.129:2000"
        ],
        body: "${label},${str}"
    )
    
    sendHubCommand(result)
    log.debug "Executing ${str}"
    log.debug result
}