import os

# print out the shell environment
env = os.environ
print(env.get('RING_FILE_GPG_BASE64'))
