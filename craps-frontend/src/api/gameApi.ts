import { PlatformClient } from '@gitgames/platform-sdk';
import type {
  GameState, RollResponse, PlaceBetResponse, RemoveBetResponse,
  CashOutResponse, CreateGameRequest, PlaceBetRequest
} from '../types/game';

export const platform = new PlatformClient();

const BASE_URL = '/api/v1/games';

function getHeaders(): Record<string, string> {
  return {
    'Content-Type': 'application/json',
    ...platform.getAuthHeaders(),
  };
}

export class ApiError extends Error {
  constructor(message: string, public code: string, public status: number) {
    super(message);
  }
}

async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const error = await response.json().catch(() => ({ error: 'UNKNOWN', message: response.statusText }));
    throw new ApiError(error.message || 'Request failed', error.error || 'UNKNOWN', response.status);
  }
  return response.json();
}

export async function createGame(request: CreateGameRequest): Promise<GameState> {
  const response = await fetch(BASE_URL, {
    method: 'POST',
    headers: getHeaders(),
    body: JSON.stringify(request),
  });
  return handleResponse<GameState>(response);
}

export async function getGame(gameId: string): Promise<GameState> {
  const response = await fetch(`${BASE_URL}/${gameId}`, { headers: getHeaders() });
  return handleResponse<GameState>(response);
}

export async function placeBet(gameId: string, request: PlaceBetRequest): Promise<PlaceBetResponse> {
  const response = await fetch(`${BASE_URL}/${gameId}/bets`, {
    method: 'POST',
    headers: getHeaders(),
    body: JSON.stringify(request),
  });
  return handleResponse<PlaceBetResponse>(response);
}

export async function removeBet(gameId: string, betId: string): Promise<RemoveBetResponse> {
  const response = await fetch(`${BASE_URL}/${gameId}/bets/${betId}`, {
    method: 'DELETE',
    headers: getHeaders(),
  });
  return handleResponse<RemoveBetResponse>(response);
}

export async function toggleBet(gameId: string, betId: string): Promise<GameState> {
  const response = await fetch(`${BASE_URL}/${gameId}/bets/${betId}/toggle`, {
    method: 'POST',
    headers: getHeaders(),
  });
  return handleResponse<GameState>(response);
}

export async function roll(gameId: string): Promise<RollResponse> {
  const response = await fetch(`${BASE_URL}/${gameId}/roll`, {
    method: 'POST',
    headers: getHeaders(),
  });
  return handleResponse<RollResponse>(response);
}

export async function cashOut(gameId: string): Promise<CashOutResponse> {
  const response = await fetch(`${BASE_URL}/${gameId}/cashout`, {
    method: 'POST',
    headers: getHeaders(),
  });
  return handleResponse<CashOutResponse>(response);
}

export async function abandonGame(gameId: string): Promise<void> {
  await fetch(`${BASE_URL}/${gameId}`, { method: 'DELETE', headers: getHeaders() });
}
