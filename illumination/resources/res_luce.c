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
int light = 0;

// Event resource definition
EVENT_RESOURCE(res_luce,
               "title=\"Light\";rt=\"\";obs",
               res_get_handler,
               res_post_handler,
               NULL,
               NULL,
               res_event_handler);


// Event handler function
static void
res_event_handler(void)
{
    // Generates random value: 1 if there is light, 0 if not
    int new_light = rand() % 2;
    if (light != new_light)
    {
        // Notify all the observers
        coap_notify_observers(&res_luce);
    }
    light = new_light;
}


// GET handler function
static void
res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset)
{
    coap_set_header_content_format(response, APPLICATION_JSON);
    snprintf((char *)buffer, COAP_MAX_CHUNK_SIZE, "{\"light\": %d}", light);
    coap_set_payload(response, buffer, strlen((char *)buffer));
}


// POST handler function
static void
res_post_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset)
{
    // Alternate the light value
    light = (light == 0) ? 1 : 0;

    // Notify all the observers about the light change
    coap_notify_observers(&res_luce);

    // Respond with the updated light
    coap_set_header_content_format(response, APPLICATION_JSON);
    snprintf((char *)buffer, COAP_MAX_CHUNK_SIZE, "{\"light\": %d}", light);
    coap_set_payload(response, buffer, strlen((char *)buffer));
}