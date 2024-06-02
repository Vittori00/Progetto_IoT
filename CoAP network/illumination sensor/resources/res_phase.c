#include "contiki.h"
#include <limits.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "coap-engine.h"

// Function declarations
static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_post_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_event_handler(void);

// Global variables
static int phase = 0;
int fase = 0;
// Event resource definition
EVENT_RESOURCE(res_phase,
               "title=\"Phase\";rt=\"\";obs",
               res_get_handler,
               res_post_handler,
               NULL,
               NULL,
               res_event_handler);


// Event handler function
static void
res_event_handler(void)
{
    // Generates random value: 0 if first phase, 1 if second phase
    int new_phase = rand() % 2;
    fase = new_phase;
    if (phase != new_phase)
    {
        // Notify all the observers
        coap_notify_observers(&res_phase);
        phase = new_phase;
    }
    
}


// GET handler function
static void
res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset)
{

    coap_set_header_content_format(response, APPLICATION_JSON);
    snprintf((char *)buffer, COAP_MAX_CHUNK_SIZE, "{\"phase\": %d}", phase);
    coap_set_payload(response, buffer, strlen((char *)buffer));
}


// POST handler function
static void
res_post_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset)
{
    // Alternate the phase value
    int new_phase = 0;
    new_phase = (phase == 0) ? 1 : 0;
    phase = new_phase;

    // Notify all the observers about the phase change
    coap_notify_observers(&res_phase);
}