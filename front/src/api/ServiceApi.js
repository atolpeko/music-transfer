import { execute } from "./ApiUtils";

export const fetchServices = () => {
  const url = `${window.SERVICE_API}/services`;
  return execute(url, { method: 'GET' });
}
