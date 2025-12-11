export function SavePath(team: string, path: string, data: string): Response {
  return Response.json({
    message: `Data received for ${team}: ${data}`,
  });
}
