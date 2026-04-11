import httpx

from app.config import settings


async def user_exists(user_id: str, token: str) -> bool:
    """Check whether a user exists in the User Service."""
    url = f"{settings.user_service_url}/users/{user_id}"
    headers = {"Authorization": f"Bearer {token}"}
    try:
        async with httpx.AsyncClient(timeout=5) as client:
            resp = await client.get(url, headers=headers)
            return resp.status_code == 200
    except httpx.RequestError:
        return False
