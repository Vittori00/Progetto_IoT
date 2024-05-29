#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "contiki.h"
#include "contiki-net.h"
#include "coap-engine.h"
#include "coap-blocking-api.h"
#include "os/dev/leds.h"

/* Log configuration */
#include "coap-log.h"
#define LOG_MODULE "App"
#define LOG_LEVEL LOG_LEVEL_APP

//#define SERVER_EP "coap://[fd00::202:2:2:2]:5683"
#define SERVER_EP "coap://[fd00::1]:5683" //localhost ip6


char *service_url_co2 = "/co2";
char *service_url_light = "/light";
char *service_url_phase = "/phase";

#define TOGGLE_INTERVAL 10
PROCESS(illumination_client, "Illumination Client");
AUTOSTART_PROCESSES(&illumination_client);

static void update_led_state();
int light_attuatore = 0;
int co2 = 0;
int fase = 0;


void client_chunk_handler(coap_message_t *response){
  const uint8_t *chunk;

  if (response == NULL){
    LOG_INFO("Request timed out");
    return;
  }

  int len = coap_get_payload(response, &chunk);
  LOG_INFO("Received %d bytes:\n", len);
  LOG_INFO("Response: %s", chunk);
}


void client_chunk_handler_co2(coap_message_t *response)
{
    const uint8_t *chunk;

    if (response == NULL)
    {
        puts("Request timed out");
        return;
    }

    coap_get_payload(response, &chunk);

    // Directly parse the JSON response to extract the CO2 level
    sscanf((const char *)chunk, "{\"co2_level\": %d}", &co2);
    printf("CO2 level: %d\n", co2);
    update_led_state();
}

void client_chunk_handler_light(coap_message_t *response)
{
    const uint8_t *chunk;

    if (response == NULL)
    {
        puts("Request timed out");
        return;
    }

    coap_get_payload(response, &chunk);

    // Directly parse the JSON response to extract the light status
    sscanf((const char *)chunk, "{\"light\": %d}", &light_attuatore);
    printf("light status: %d\n", light_attuatore);
    update_led_state();
}

void client_chunk_handler_phase(coap_message_t *response)
{
    const uint8_t *chunk;

    if (response == NULL)
    {
        puts("Request timed out");
        return;
    }

    coap_get_payload(response, &chunk);

    // Directly parse the JSON response to extract the light_attuatore status
    sscanf((const char *)chunk, "{\"phase\": %d}", &fase);
    printf("Phase: %d\n", fase);
    update_led_state();
}
void handle_notification_co2(struct coap_observee_s *observee, void *notification, coap_notification_flag_t flag)
{
    coap_message_t *msg = (coap_message_t *)notification;
    if (msg)
    {
        printf("Received notification ");
        client_chunk_handler_co2(msg);
    }
    else
    {
        printf("No notification received\n");
    }
}
void handle_notification_light(struct coap_observee_s *observee, void *notification, coap_notification_flag_t flag)
{
    coap_message_t *msg = (coap_message_t *)notification;
    if (msg)
    {
        printf("Received notification ");
        client_chunk_handler_light(msg);
    }
    else
    {
        printf("No notification received\n");
    }
}
void handle_notification_phase(struct coap_observee_s *observee, void *notification, coap_notification_flag_t flag)
{
    coap_message_t *msg = (coap_message_t *)notification;
    if (msg)
    {
        printf("Received notification ");
        client_chunk_handler_phase(msg);
    }
    else
    {
        printf("No notification received\n");
    }
}

void update_led_state()
{
    leds_off(LEDS_ALL); // Turn off all LEDs initially

    if (light_attuatore == 1 && co2 > 400 && co2 < 1500 && fase == 0)
    {
        leds_off(LEDS_ALL); // LED OFF
    }
    else if (light_attuatore == 1 && (co2 < 400 || co2 > 1500) && fase == 0)
    {
        leds_on(LEDS_YELLOW); // Yellow LED ON
    }
    else if (light_attuatore == 0 && co2 > 400 && co2 < 1500 && fase == 0)
    {
        leds_on(LEDS_RED); // Red LED Blinking
    }
    else if (light_attuatore == 0 && (co2 < 400 || co2 > 1500) && fase == 0)
    {
        leds_on(LEDS_RED); // Red LED ON
    }
    else if (light_attuatore == 1 && co2 < 400 && fase == 1)
    {
        leds_off(LEDS_ALL); // LED OFF
    }
    else if (light_attuatore == 1 && (co2 < 400 || co2 > 1500) && fase == 1)
    {
        leds_on(LEDS_GREEN); // Green LED ON
    }
    else
    {
        leds_on(LEDS_GREEN); // Blue LED ON
    }
}

PROCESS_THREAD(illumination_client, ev, data)
{
    static coap_endpoint_t server_ep;
    static coap_message_t request[1];

    PROCESS_BEGIN();

    // get iniziale per avviare lo stato iniziale della luce
    coap_endpoint_parse(SERVER_EP, strlen(SERVER_EP), &server_ep);
    coap_init_message(request, COAP_TYPE_CON, COAP_GET, 0);
    
    // CO2
    coap_set_header_uri_path(request, service_url_co2);
    COAP_BLOCKING_REQUEST(&server_ep, request, client_chunk_handler_co2);
    // Light
    coap_set_header_uri_path(request, service_url_light);
    COAP_BLOCKING_REQUEST(&server_ep, request, client_chunk_handler_light);
    // Phase
    coap_set_header_uri_path(request, service_url_phase);
    COAP_BLOCKING_REQUEST(&server_ep, request, client_chunk_handler_phase);
    // chiamata funzione cambio luci dati i primi parametri trovati
    update_led_state();

    // REGISTRATION PER CO2
    coap_set_header_uri_path(request, service_url_co2);
    coap_obs_request_registration(&server_ep, service_url_co2, handle_notification_co2, NULL);

    // REGISTRATION PER light
    coap_set_header_uri_path(request, service_url_light);
    coap_obs_request_registration(&server_ep, service_url_light, handle_notification_light, NULL);

    // REGISTRATION PER phase
    coap_set_header_uri_path(request, service_url_phase);
    coap_obs_request_registration(&server_ep, service_url_phase, handle_notification_phase, NULL);

    // Registration
    coap_set_header_uri_path(request, "registration");
    LOG_INFO("Registering to the CoAP Server\n");
    COAP_BLOCKING_REQUEST(&server_ep, request, client_chunk_handler);

    while (1)
    {
        PROCESS_YIELD();
    }
    PROCESS_END();
}
